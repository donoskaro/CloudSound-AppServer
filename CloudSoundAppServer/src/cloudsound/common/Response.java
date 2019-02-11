/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common;

import java.util.HashMap;

/**
 *
 * @author UBlavins
 */
public class Response {
    
    public static int OK = 200;
    public static int CREATED = 201;
    public static int NO_CONTENT = 204;
    public static int PARTIAL_CONTENT = 206;
    
    public static int BAD_REQUEST = 400;
    public static int UNAUTHORIZED = 401;
    public static int FORBIDDEN = 403;
    public static int NOT_FOUND = 404;
    public static int LENGTH_REQUIRED = 411;
    public static int UNSUPPORTED_MEDIA_TYPE = 415;
    public static int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
    public static int CHECKSUM_VERIFICATION_FAILED = 471; // X7X are app specific error codes
    public static int INVALID_CONTROLLER_OR_ACTION = 472;
    
    public static int INTERNAL_SERVER_ERROR = 500;
    public static int SERVICE_UNAVAILABLE = 503;
    
    private int statusCode;
    private HashMap<String, String> headers = new HashMap();
    private String content = "";
    
    private boolean mapInit = false;
    private HashMap<Integer, String> reverseCodeMap = new HashMap();
    
    public Response(int status) {
        statusCode = status;
    }
    
    public Response(int status, HashMap<String, String> headerList) {
        statusCode = status;
        headers = headerList;
    }
    
    public Response(int status, HashMap<String, String> headerList, String body) {
        statusCode = status;
        headers = headerList;
        content = body;
    }
    
    //update header or create
    // DEPRECATED
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
    
    public void header(String key, String value) {
        headers.put(key, value);
    }
    
    public void deleteHeader(String key) {
        headers.remove(key);
    }
    
    public int status() {
        return statusCode;
    }
    
    public HashMap<String, String> header() {
        return headers;
    } 
    
    public String body() {
        return content;
    }
    
    public void body(String newBody) {
        content = newBody;
    }
    
    public String output() {
        String text = statusCode + " " + getUserFriendlyCode(statusCode) + "\n";
        for (String key: headers.keySet()) {
            text += key + ": "  + headers.get(key) + "\n";
        }
        text += "\n";
        text += content;
        return text;
    }
    
    private String getUserFriendlyCode(int code) {
        if(mapInit == false) {
            HashMap<Integer, String> map = new HashMap();

            map.put(200, "OK");
            map.put(201, "Created");
            map.put(204, "No Content");
            map.put(206, "Partial Content");
            map.put(400, "Bad Request");
            map.put(401, "Unauthorized");
            map.put(403, "Forbidden");
            map.put(404, "Not Found");
            map.put(411, "Length Required");
            map.put(415, "Unsupported Media Type");
            map.put(431, "Request Header Fields Too Large");
            map.put(471, "Checksum Verification Failed");
            map.put(472, "Invalid Controller or Action");
            map.put(500, "Internal Server Error");
            map.put(503, "Service Unavailable");
            
            reverseCodeMap = map;
        }
        
        return reverseCodeMap.get(code);
    }
    
}
