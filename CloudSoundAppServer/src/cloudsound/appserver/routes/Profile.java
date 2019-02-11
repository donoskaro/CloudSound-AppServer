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
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author UBlavins
 */
public class Profile {
    
    public Response Get(Message m) {
        Json o;
        Response r;
        String sessionKey, outputJson, sql;
        HashMap<Integer, HashMap<String, String>> res;
        //id, username, email, bio
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "';";
        try {
            res = Database.fetch(sql);
        } catch(SQLException e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // return profile
       
        sql = "SELECT username, email, bio FROM user " + 
                "WHERE id = (SELECT user FROM active_user WHERE session_key = '" + sessionKey + "');";
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
    
    public Response Post(Message m) {
        Json o;
        Response r;
        String sessionKey, outputJson, sql;
        String username, email, bio, password;
        HashMap<Integer, HashMap<String, String>> res;
        int changedData = 0;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        sessionKey = m.header("Session-Key");
        username   = o.getAsString("username");
        email      = o.getAsString("email");
        bio        = o.getAsString("bio");
        password   = o.getAsString("password");
        
        
        if (!username.equals("")) {
            changedData += 1;
            sql = "UPDATE user SET username = '" + username + "' WHERE id = (SELECT user FROM active_user WHERE session_key = '" +
                    sessionKey + "');";
            try {
                Database.query(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
        }
        
        if (!email.equals("")) {
            changedData += 1;
            sql = "UPDATE user SET email = '" + email + "' WHERE id = (SELECT user FROM active_user WHERE session_key = '" +
                    sessionKey + "');";
            try {
                Database.query(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
        }
        
        if (!bio.equals("")) {
            changedData += 1;
            sql = "UPDATE user SET bio = '" + bio + "' WHERE id = (SELECT user FROM active_user WHERE session_key = '" +
                    sessionKey + "');";
            try {
                Database.query(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
        }
        
        
        if (!password.equals("")) {
            // create a password hash
            changedData += 1;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes());
                password = DatatypeConverter.printHexBinary(md.digest());
            } catch (Exception e) {
                // error hashing password
                System.out.println(e.toString());
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
            sql = "UPDATE user SET username = '" + username + "' WHERE id = (SELECT user FROM active_user WHERE session_key = '" +
                    sessionKey + "');";
            try {
                Database.query(sql);
            } catch(SQLException e) {
                return new Response(Response.INTERNAL_SERVER_ERROR);
            }
        }
        
        if (changedData == 0) {
            return new Response(Response.BAD_REQUEST);
        }
        
        return new Response(Response.OK);
    }
    
}
