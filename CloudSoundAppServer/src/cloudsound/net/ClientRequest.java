/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.net;

import java.util.HashMap;
/**
 *
 * @author oskar
 */
public class ClientRequest {
    
    private String controller;
    private String action;
    private HashMap<String, String> headers = new HashMap();
    private String body = "";
    
    public ClientRequest() {
    
    }
    
    public void controller(String c) {
        controller = c;
    }
    
    public void action(String a) {
        action = a;
    }
    
    public void header(String key, String value) {
        headers.put(key, value);
    }
    
    public void body(String b) {
        body = b;
    }
    
    public String output() {
        String output = controller + ":" + action + "\n";
        
        for(String key : headers.keySet()) {
            output += key + ": " + headers.get(key) + "\n";
            
            if(!body.equals("")) {
                try {
                    output += "Content-Length: " + String.valueOf(body.getBytes("UTF-8").length) + "\n";
                } catch (Exception e) {}
            }
        }
        
        output += "\n";
        
        output += body;
        
        return output;
    }
    
}
