/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver.routes;

import cloudsound.common.Response;
import cloudsound.common.Message;
import cloudsound.common.util.Json;
import cloudsound.common.Database;

import java.util.HashMap;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import java.util.UUID;

/**
 *
 * @author oskar
 */
public class User {
    
    public Response Login(Message m) { 
        Json o;
        Response r;
        String username,
               password,
               sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        username = o.getAsString("username");
        password = o.getAsString("password");
        
        // check if empty
        if(username.equals("") || password.equals("")) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            
            return r;
        }
        
        // create a password hash
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            password = DatatypeConverter.printHexBinary(md.digest());
        } catch (Exception e) {
            // error hashing password
            System.out.println(e.toString());
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        // check if credentials match
        sql = "SELECT id FROM user WHERE username = '" + username + "' AND password = '" + password + "'";
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
            System.out.println(e.toString());
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() != 1) {
            // account already exists
            r = new Response(Response.UNAUTHORIZED);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Incorrent username or password.\", \"error_code\": \"INCORRECT_CREDENTIALS\"}");
            
            return r;
        }
        
        // get the user id
        String userId = res.get(0).get("id");
        
        // create a random alphanumeric session key
        String sessionKey = UUID.randomUUID().toString().replace("-", "");
        long loginTime    = (new java.util.Date()).getTime();
        
        // insert into the active_user table
        sql = "INSERT INTO active_user (id, user, login_time, session_key) VALUES ('" + UUID.randomUUID().toString().replace("-", "") + "', '" + userId + "', '" + loginTime + "', '" + sessionKey + "')";
        try {
            Database.query(sql);
        } catch(Exception e) {
            System.out.println(e.toString());
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body("{\"status\": \"success\", \"sessionKey\": \"" + sessionKey + "\"}");
        
        return r;
    }
    
    public Response Register(Message m) {
        Json o;
        Response r;
        String username,
               email,
               password,
               sql,
               resourceId;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get json object
        try {
            o = new Json(m.body());
        } catch (Exception e) {
            return new Response(Response.BAD_REQUEST);
        }
        
        // get details
        username = o.getAsString("username");
        email    = o.getAsString("email");
        password = o.getAsString("password");
        
        // check if empty
        if(username.equals("") || email.equals("") || password.equals("")) {
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Not all required fields have been provided.\", \"error_code\": \"FIELD_MISSING\"}");
            
            return r;
        }
        
        // check if account with that username or email already exists.
        sql = "SELECT id FROM user WHERE username = '" + username + "' OR email = '" + email + "'";
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() > 0) {
            // account already exists
            r = new Response(Response.BAD_REQUEST);
            r.header("Content-Type", "application/json");
            r.body("{\"status\": \"error\", \"error\": \"Account already exists.\", \"error_code\": \"ACCOUNT_EXISTS\"}");
            
            return r;
        }
        
        // hash the password
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            password = DatatypeConverter.printHexBinary(md.digest());
        } catch (Exception e) {
            // error hashing password
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        resourceId = UUID.randomUUID().toString().replace("-", "");
        
        // create the new user account
        sql = "INSERT INTO user (id, username, email, password) VALUES ('" + resourceId + "', '" + username + "', '" + email + "', '" + password + "')";
        try {
            Database.query(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        return new Response(Response.CREATED);
    }
    
    public Response Logout(Message m) {
        Json o;
        Response r;
        String sessionKey,
               sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if header is actually defined
        if(sessionKey.equals("")) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "'";
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        sql = "DELETE FROM active_user WHERE session_key = '" + sessionKey + "'";
        try {
            Database.query(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        return new Response(Response.OK);        
    }
    
    public Response FetchOnlineUsers(Message m) {
        Json o;
        Response r;
        String sessionKey,
               sql,
               outputJson;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if header is actually defined
        if(sessionKey.equals("")) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check if key exists in database
        sql = "SELECT id FROM active_user WHERE session_key = '" + sessionKey + "'";
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        sql = "SELECT a.user, u.username, a.login_time FROM active_user a, user u WHERE a.user = u.id GROUP BY a.user";
        
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
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
    
    public Response IsValidSession(Message m) {
        String sessionKey,
               sql;
        HashMap<Integer, HashMap<String, String>> res;
        
        // get details
        sessionKey = m.header("Session-Key");
        
        // check if header is actually defined
        if(sessionKey.equals("")) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        // check if key exists in database
        sql = "SELECT user FROM active_user WHERE session_key = '" + sessionKey + "'";
        try {
            res = Database.fetch(sql);
        } catch(Exception e) {
            return new Response(Response.INTERNAL_SERVER_ERROR);
        }
        
        if(res.size() == 0) {
            return new Response(Response.UNAUTHORIZED);
        }
        
        String userId = res.get(0).get("user");
        
        Response r = new Response(Response.OK);
        r.header("Content-Type", "application/json");
        r.body("{\"status\": \"ok\", \"userId\": \"" + userId + "\"}");
        
        return r;
    }
    
}