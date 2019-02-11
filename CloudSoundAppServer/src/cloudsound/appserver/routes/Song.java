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

/**
 *
 * @author UBlavins
 */
public class Song {
    
    public Response ListGenres(Message m) {
        Response r;
        String sessionKey, outputJson, sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // return list of genres
        sql = "SELECT name FROM genre";
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
        Json o;
        Response r;
        String genre,sessionKey, outputJson,sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        genre      = o.getAsString("genre");
        
        // check if genre is in genres
        sql = "SELECT id FROM genre WHERE name = '" + genre + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (res.isEmpty()) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            return r;
        }
        
        // return list of songs from user
        sql = "SELECT id, name, genre, length, artist, album, user, num_downloads FROM song" +
                "WHERE user = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');" ;
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // return list of songs from friends
        sql = "SELECT  s.id, s.name, s.length, s.artist, s.album, t.user, s.num_downloads FROM song s" +
                "(SELECT user1 as friend FROM friend WHERE user2 = (SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "') UNION" +
                "SELECT user2 as friend FROM friend WHERE user1 = (SELECT user FROM active_user WHERE session_key ='" +sessionKey+ "')) t" +
                "WHERE t.user = s.user;";
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
    
    public Response ListOwn(Message m) {
        Json o;
        Response r;
        String genre,sessionKey, outputJson,sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        genre      = o.getAsString("genre");
        
        // check if genre is in genres
        sql = "SELECT id FROM genre WHERE name = '" + genre + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if (res.isEmpty()) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            return r;
        }
        
        // return list of songs
        sql = "SELECT id, name, genre, length, artist, album, user, num_downloads FROM song" +
                "WHERE user = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');" ;
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
    
}
