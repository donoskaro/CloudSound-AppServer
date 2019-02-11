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
public class Friend {
    
    public Response Request(Message m) {
        Json o;
        Response r;
        String resourceId, username,email, sessionKey, sql;
        String user_id;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        user_id  = o.getAsString("user");
        username = o.getAsString("username");
        email    = o.getAsString("email");
        
        // check if empty
        if(user_id == null || username.equals("") || email.equals("")) {
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
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check if user is in database
        sql = "SELECT id FROM user where id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (res.isEmpty()) {
            return new Response(Response.NOT_FOUND);
        }
        
        resourceId = UUID.randomUUID().toString().replace("-", "");
        
        // make a new friend request
        sql = "INSERT INTO friend_request (id, sender, recipient, time_sent) VALUES" +
                "('"+ resourceId + "',(SELECT username FROM users WHERE id = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "'), '" +
                username + "'," +(new java.util.Date()).getTime() + ");";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        return new Response(Response.OK);
    }
    
    public Response Accept(Message m) {
        Json o;
        Response r;
        String resourceId, sessionKey, sql;
        String friend_request_id;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        friend_request_id  = ("friend_request_id");
        
        // check if empty
        if(friend_request_id == null) {
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
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
         // check if friend request is in database
        sql = "SELECT id FROM friend_request WHERE id = " + friend_request_id + ";";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (res.isEmpty()) {
            return new Response(Response.NOT_FOUND);
        }
        
        sql = "SELECT id FROM friend WHERE (user1 = (SELECT sender FROM friend_request WHERE id = '" + friend_request_id +
                "') AND user2 = (SELECT recipient FROM friend_request WHERE id = '" + friend_request_id +
                "') OR (user1 = (SELECT recipient FROM friend_request WHERE id = '" + friend_request_id +
                "AND user2 = (SELECT sender FROM friend_request WHERE id = '" + friend_request_id + "')";
        
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        resourceId = UUID.randomUUID().toString().replace("-", "");
        
        if (res.isEmpty()) {
            sql = "INSERT INTO friend (id, user1, user2) VALUES ('" + resourceId +
                    "', (SELECT sender FROM friend_request WHERE id = '" + friend_request_id +
                    "'), (SELECT recipient FROM friend_request WHERE id = '" + friend_request_id +"'))";
            try {
                Database.query(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
        }
        
        sql = "DELETE FROM friend_request WHERE id = '" + friend_request_id + "'";
        try {
            Database.query(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        return new Response(Response.OK);
    }
    
    public Response Remove(Message m) {
        Json o;
        Response r;
        String resourceId, username,email, sessionKey, sql;
        String user_id;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        user_id  = o.getAsString("user");
        username = o.getAsString("username");
        email    = o.getAsString("email");
        
        // check if empty
        if(user_id == null || username.equals("") || email.equals("")) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            
            return r;
        }
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "'";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check if user is in database
        sql = "SELECT id FROM user where id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "'";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (res.isEmpty()) {
            return new Response(Response.NOT_FOUND);
        }
        
        // check if user is friends with active user, 
        sql = "SELECT id FROM friend WHERE (user1 = '" + user_id + "' AND user2 = '(SELECT id FROM active_user WHERE session_key = '" + sessionKey +
                "')) OR (user1 = (SELECT id FROM active_user WHERE session_key = '" + sessionKey + "') AND user2 = '" + user_id + "')";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        if (res.isEmpty()) {
            sql = "SELECT id FROM friend_request WHERE sender ='" + user_id + "' AND recipient = '(SELECT id FROM active_user WHERE session_key = '" + 
                    sessionKey + "'));";
            try {
                res = Database.fetch(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
            
            if (res.isEmpty()) {
                return new Response(Response.BAD_REQUEST);
            } else {
                sql = "DELETE FROM friend_request WHERE sender = '" + user_id + "'AND recipient = '(SELECT user FROM active_user WHERE session_key = '" + 
                        sessionKey  + "')';";
                try {
                    Database.query(sql);
                } catch(SQLException e) {
                    return new Response(Response.INTERNAL_SERVER_ERROR);
                }
            }
            
        }
         
        // delete friend
        sql = "DELETE FROM friend WHERE (user1 = '" + user_id + "' AND user2 = '(SELECT user FROM active_user WHERE session_key = '" + sessionKey  + 
                "')') OR (user2 = user_id AND user1 = '(SELECT user FROM active_user WHERE session_key = '" + sessionKey  + "')');";
        try {
            Database.query(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        return new Response(Response.OK);
    }
    
    public Response ListRequest(Message m) {
        Response r;
        String sessionKey, outputJson, sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        sql = "SELECT * FROM friend_request WHERE recipient = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        outputJson = "[";
        
        for(HashMap<String, String> row : res.values()) {
            outputJson += "{";
            boolean hasComma = false;
            
            for(String key : row.keySet()) {
                outputJson += "\"" + key + "\": \"" + row.get(key) + "\",";
                hasComma = true;
            }
            
            if(hasComma) {
                // need to remove the comma
                outputJson.substring(0, outputJson.length() - 1);
            }
            
            outputJson += "}";
        }
        
        outputJson += "]";
        
        r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body(outputJson);
        
        return r;
    }
    
    public Response List(Message m) {
        Response r;
        String sessionKey, outputJson, sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        sql = "SELECT t.friend, u.username FROM user u, " +
                "(SELECT user1 as friend FROM friend WHERE user2 = (SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "') UNION" +
                "SELECT user2 as friend FROM friend WHERE user1 = (SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "')) t" +
                "WHERE t.friend = u.id;";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        outputJson = "[";
        
        for(HashMap<String, String> row : res.values()) {
            outputJson += "{";
            boolean hasComma = false;
            
            for(String key : row.keySet()) {
                outputJson += "\"" + key + "\": \"" + row.get(key) + "\",";
                hasComma = true;
            }
            
            if(hasComma) {
                // need to remove the comma
                outputJson.substring(0, outputJson.length() - 1);
            }
            
            outputJson += "}";
        }
        
        outputJson += "]";
        
        r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body(outputJson);
        
        return r;
    }
    
    public Response GetProfile(Message m) {
        Json o;
        Response r;
        String resourceId, username,email, sessionKey, sql, outputJson;
        String user_id;
        HashMap<Integer, HashMap<String, String>> res;
        outputJson = "[";
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        user_id  = o.getAsString("user");
        username = o.getAsString("username");
        email    = o.getAsString("email");
        
        // check if empty
        if(user_id.equals("") || username.equals("") || email.equals("")) {
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
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check other user profile
        sql = "SELECT id, username,bio FROM user WHERE id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (!res.isEmpty()) {
            for(HashMap<String, String> row : res.values()) {
                outputJson += "{";
                boolean hasComma = false;

                for(String key : row.keySet()) {
                    outputJson += "\"" + key + "\": \"" + row.get(key) + "\",";
                    hasComma = true;
                }

                if(hasComma) {
                    // need to remove the comma
                    outputJson.substring(0, outputJson.length() - 1);
                }

                outputJson += "}";
            }
        } else {
            return new Response(Response.NOT_FOUND);
        }
        
        // check list of posts by user
        sql = "SELECT id, data, type, time FROM post WHERE user = '" +
                "'(SELECT id FROM user WHERE id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "')';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (!res.isEmpty()) {
            for(HashMap<String, String> row : res.values()) {
                outputJson += "{";
                boolean hasComma = false;

                for(String key : row.keySet()) {
                    outputJson += "\"" + key + "\": \"" + row.get(key) + "\",";
                    hasComma = true;
                }

                if(hasComma) {
                    // need to remove the comma
                    outputJson.substring(0, outputJson.length() - 1);
                }

                outputJson += "}";
            }
        } 
        
        // check if they are friends
        sql = "SELECT id FROM friend WHERE user 1 = '" + 
                "(SELECT id FROM user WHERE (id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "')'" +
                "AND user2 = '(SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "')' AND ( user1 = '" +
                "(SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "')' AND user2 = '" +
                "(SELECT id FROM user WHERE (id = '" + user_id + "' OR username = '" + username + "' OR email = '" + email + "')');";
        
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (!res.isEmpty()) {
            outputJson += "{";
            outputJson += "\"" + "friend" + "\": \"" + "yes" + "\",";
            outputJson += "}";
        } else {
            outputJson += "{";
            outputJson += "\"" + "friend" + "\": \"" + "no" + "\",";
            outputJson += "}";
        }
        
        outputJson += "]";
        r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body(outputJson);
        return r;
    }
    
}
