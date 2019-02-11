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
public class ClientResponse {
    
    private String textResponse;
    private HashMap<String, String> headers = new HashMap();
    private String body;
    private int statusCode;
    
    public ClientResponse(String text) {
        String firstPart = text.split("\n\n")[0];
        
        
        String lines[] = firstPart.split("\n");
        
        String firstLine[] = lines[0].split("0");
        
        try {
            statusCode = Integer.parseInt(firstLine[0].trim());
        } catch (Exception e) {
            statusCode = 500;
        }

        int i = 1;
        while(i < lines.length) {
            String header[] = lines[i].split(":");
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            } else {
                headers.put("", "");
            }
            i++;
        }
        
        body = text.replace(firstPart + "\n\n", "");
    }
    
    public String body() {
        return body;
    }
    
    public int statusCode() {
        return statusCode;
    }
    
    public String header(String key) {
        if(headers.containsKey(key)) {
            return headers.get(key);
        } else {
            return "";
        }
    }
    
}
