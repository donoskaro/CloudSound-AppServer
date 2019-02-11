/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver.routes;

import cloudsound.common.Database;
import cloudsound.common.Message;
import cloudsound.common.Response;
import cloudsound.common.util.Json;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author UBlavins
 */
public class Social {
    
    /*
   
    Social:GetFeedPosts -> Gets 20 latest posts from own feed + all friends. 
    JSON Input: none. JSON Output: list of posts: [title, content, post ID, author id, time posted, name of author]. 
    Return status if successful: Request.OK
    
    */
    
    public Response CreatePost(Message m) {
        Json o;
        Response r;
        String title, content, username, sessionKey, sql, resourceId;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        title    = o.getAsString("title");
        content  = o.getAsString("content");
        
        // check if null
        if (title.equals("") || content.equals("")) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            return r;
        }
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            System.out.println(e);
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        resourceId = UUID.randomUUID().toString().replace("-", "");
        
        // create the new post
        sql = "INSERT INTO post (id, title, content, user, time) VALUES ('" + resourceId + "', '" + title + "', '" + content + 
                "', (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "'), '" + (new java.util.Date()).getTime() + "')";
        try {
            Database.query(sql);
        } catch(SQLException e) {
            System.out.println(e);
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        r = new Response(Response.CREATED);
        r.body("{\"status\": \"ok\", \"id\": \"" + resourceId + "\", \"title\": \"" + title + "\", \"content\": \"" + content + "\"}");
        return r;
    }
    
    public Response DeletePost(Message m) {
        Json o;
        Response r;
        String sessionKey, sql, post_id;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        post_id = o.getAsString("post_id");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // check if post_id is users
        sql = "SELECT * FROM post WHERE id = '" + post_id + "' AND user = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // delete user post
        // this is to make sure fully
        sql = "DELETE FROM post WHERE id = '" + post_id + "';";
        try {
            Database.query(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        return new Response(Response.OK);
    }
    
    public Response SharePost(Message m) {
        Json o;
        Response r;
        String sessionKey, sql, post_id, resourceId;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        post_id = o.getAsString("post_id");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // check if user is friends with other user
        sql = "SELECT * FROM post WHERE user1 = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "') AND " 
        + "user2 = (SELECT user FROM post WHERE id = '" + post_id + "');";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        resourceId = UUID.randomUUID().toString().replace("-", "");
        
        // share post
        sql = "INSERT INTO post_share (id, post, user) VALUES ('" + resourceId + "', '" + post_id + "',(SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');";
        try {
            Database.query(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        return new Response(Response.OK);
    }
    
    public Response GetFeedPosts(Message m) {
        Response r;
        String sessionKey, sql, outputJson;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            System.out.println(e);
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        sql = "SELECT p.id, p.title, p.content, p.user, p.time, u.username FROM post p, user u WHERE p.user = u.id AND (p.user IN (" +
              " SELECT user1 as friend FROM friend WHERE user2 = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "') UNION" +
              " SELECT user2 as friend FROM friend WHERE user1 = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "')" +
              ") OR p.user = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "')) ORDER BY p.time DESC LIMIT 20";
        
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            System.out.println(e);
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
       
        outputJson = "[";
        boolean hasComma1 = false;
        
        for(HashMap<String, String> row : res.values()) {
            outputJson += "{";
            hasComma1 = true;
            boolean hasComma2 = false;
            
            for(String key : row.keySet()) {
                outputJson += "\"" + key + "\": \"" + row.get(key) + "\",";
                hasComma2 = true;
            }
            
            if(hasComma2) {
                // need to remove the comma
                outputJson = outputJson.substring(0, outputJson.length() - 1);
            }
            
            outputJson += "},";
        }
        
        if(hasComma1) {
            outputJson = outputJson.substring(0, outputJson.length() - 1);
        }
        
        outputJson += "]";
        
        r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body(outputJson);
        
        return r;
    }
    
}
