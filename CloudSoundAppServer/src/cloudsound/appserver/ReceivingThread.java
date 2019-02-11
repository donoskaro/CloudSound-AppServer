/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver;

import cloudsound.common.ReceivingQueue;
import cloudsound.common.SocketStore;
import cloudsound.common.Message;
import cloudsound.common.MessageQueue;
import cloudsound.common.Response;

import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 * @author oskaraugustyn
 */
public class ReceivingThread implements Runnable {
    
    // code inspired by: https://www.tutorialspoint.com/java/java_multithreading.htm
    private Thread t;
    private String threadName;
    
    public boolean activeLoop = true;
    
    final private static int MAXIMUM_BODY_SIZE       = 67108864; // 64M
    final private static int MAXIMUM_HEADER_SIZE     = 8192; // 8K
    final private static int CONNECTION_TIMEOUT      = 30 * 1000; // 30s in ms
    final private static int MAXIMUM_CONNECTION_TIME = 90 * 1000; // 90s in ms
    final private static String CONTENT_ENCODING     = "UTF-8";
    
    public ReceivingThread(String name) {
        threadName = name;
    }
    
    @Override
    public void run() {
        while(activeLoop) {
            // fetch items
            String socketRef = ReceivingQueue.dequeue();
            Socket conn = SocketStore.get(socketRef);
            
            long timeStart = (new java.util.Date()).getTime();
            long timeoutStart = (new java.util.Date()).getTime();
            
            String headers;
            InputStream connInputStream;
            byte[] buffer;
            byte previousChar = 0;
            byte curChar;
            int bufferIndex = 0;
            try {
                connInputStream = conn.getInputStream();
                
                // max 8KB for headers (same as Apache limit). Could increase by having a dynamically
                // sized buffer
                buffer = new byte[MAXIMUM_HEADER_SIZE];
                
                // loop through bytes
                while(true) {
                    if(bufferIndex == MAXIMUM_HEADER_SIZE) {
                        // size exceeded 8KB, must close connection.
                        //TODO: return headers too large error (entity too large)
                        throw new IOException();
                    }
                    
                    if((new java.util.Date()).getTime() > timeoutStart + CONNECTION_TIMEOUT
                            || (new java.util.Date()).getTime() > timeStart + MAXIMUM_CONNECTION_TIME) {
                        throw new IOException();
                    }
                    
                    // is this resource intensive? A while loop just checking the date.
                    if(connInputStream.available() > 0) {
                        curChar = (byte) connInputStream.read();

                        if(previousChar == curChar && (new String(new byte[] {curChar}, CONTENT_ENCODING)).equals("\n")) {
                            break;
                        }

                        previousChar = curChar;

                        buffer[bufferIndex] = curChar;
                        bufferIndex++;
                        
                        timeoutStart = (new java.util.Date()).getTime();
                    }
                }
                
                headers = new String(buffer, 0, bufferIndex, CONTENT_ENCODING);
                
            } catch (IOException e) {
                SocketStore.remove(socketRef);
                try {
                    conn.close();
                } catch (IOException f) {}
                continue;
            }
            
            // end of headers
            // now need to check if the Content-Length header exists.
            int contentLength = getContentLength(headers);
        
            String body = "";
            
            if(contentLength > MAXIMUM_BODY_SIZE) {
                // return request too large error
                SocketStore.remove(socketRef);
                try {
                    conn.close();
                } catch (IOException f) {}
                continue;
            }
            
            // now read the body (if len > 0)
            if(contentLength > 0) {
                try {
                    buffer = new byte[contentLength];
                    connInputStream.read(buffer, 0, contentLength);

                    body = new String(buffer, CONTENT_ENCODING);
                } catch (IOException e) {
                    // well, something went wrong
                    SocketStore.remove(socketRef);
                    try {
                        conn.close();
                    } catch (IOException f) {}
                    continue;
                }
            }
        
            String message = headers + "\n" + body;
       
            Message msg = new Message(socketRef, message);
            MessageQueue.enqueue(msg);
        }
    }
    
    public void start() {
        if(t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    private int getContentLength(String headers) {
        // split by new line, loop through
        // split by ":" and check if first element is "Content-Length"
        // if yes then get the second part and convert to an integer and return it
        String lines[] = headers.split("\n");
        for (String line : lines) {
            String[] header = line.split(":");
            if (header[0].equals("Content-Length")) {
                return Integer.parseInt(header[1].trim());
            }
        }
        return 0;
    }
    
    // purpose of this is so that we can return an error message without having
    // to go through the whole system.
    private void returnResponse(Socket sock, int code) {
        Response r = new Response(code);
        
        //TODO: Finish implementation
    }
    
}
