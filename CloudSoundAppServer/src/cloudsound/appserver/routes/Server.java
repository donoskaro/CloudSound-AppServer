/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver.routes;

import cloudsound.common.Response;
import cloudsound.common.Message;

import java.util.HashMap;

/**
 *
 * @author oskaraugustyn
 */
public class Server {
    
    public Response HelloWorld(Message m) {        
        HashMap<String, String> headers = new HashMap();
        headers.put("Content-Type", "text/plain");
        
        return new Response(200, headers, "Hello, World!");
        
        // TODO: Response should have methods such as .contentType("text/plain");
        //       as well as .header("key", "value");
    }
    
    public Response EmojiTest(Message m) {
        HashMap<String, String> headers = new HashMap();
        headers.put("Content-Type", "text/plain");
        
        return new Response(200, headers, "😂😂😂");
    }
    
}
