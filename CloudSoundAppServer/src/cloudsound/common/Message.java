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

public class Message {
    private String socketStr = "";
    private String controllerStr = "";
    private String actionStr;
    private HashMap<String, String> headersStr = new HashMap();
    private String bodyStr;
    
    // content-length: issue with server
    
    public Message(String socket, String message) {
        socketStr = socket;
        String firstPart = message.split("\n\n")[0];
        
        
        String lines[] = firstPart.split("\n");
        
        String firstLine[] = lines[0].split(":");
        if (firstLine.length == 2) {
            controllerStr = firstLine[0].trim();
            actionStr = firstLine[1].trim();
        }

        int i = 1;
        while(i < lines.length) {
            String header[] = lines[i].split(":");
            if (header.length == 2) {
                headersStr.put(header[0].trim(), header[1].trim());
            } else {
                headersStr.put("", "");
            }
            i++;
        }
        
        bodyStr = message.replace(firstPart + "\n\n", "");
    }
    
    public String controller() {
        return controllerStr;
    }
    
    public String action() {
        return actionStr;
    }
    
    public HashMap<String, String> headers() {
        return headersStr;
    }
    
    public String header(String Key) {
        if (headersStr.containsKey(Key)) {
            return headersStr.get(Key);
        }
        return "";
    }
    
    public String body() {
        if (bodyStr.length() > 0) {
            return bodyStr;
        }
        return "";
    }
    
    public String socketRef() {
        return socketStr;
    }
}
