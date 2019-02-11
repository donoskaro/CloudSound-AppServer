/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.net;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author oskar
 */
public class Client {
    
    final private static String CONTENT_ENCODING = "UTF-8";
    final private static int MAXIMUM_BODY_SIZE   = 67108864; // 64M
    final private static int MAXIMUM_HEADER_SIZE = 8192; // 8K
    
    private Socket sock;
    private String textResponse;
    
    public Client() {
        
    }
    
    public void connectTo(String hostname, int port) throws Exception {
        try {
            sock = new Socket(hostname, port);
        } catch (Exception e) {
            throw new Exception();
        }
    }
    
    public boolean sendMessage(ClientRequest r) {
        try {
            OutputStream outputStream = sock.getOutputStream();
            String outputBody = r.output();
            outputStream.write(outputBody.getBytes(CONTENT_ENCODING));
            
            saveTextResponse();
        } catch(Exception e) {
            return false;
        }
        
        return true;
    }
    
    public ClientResponse getResponse() {
        return new ClientResponse(textResponse);
    }
    
    private void saveTextResponse() {
        String headers;
        InputStream connInputStream;
        byte[] buffer;
        byte previousChar = 0;
        byte curChar;
        int bufferIndex = 0;
        
        try {
            connInputStream = sock.getInputStream();
                
            buffer = new byte[MAXIMUM_HEADER_SIZE];
                
            // loop through bytes
            while(true) {
                if(bufferIndex == MAXIMUM_HEADER_SIZE) {
                    throw new Exception();
                }
                
                curChar = (byte) connInputStream.read();

                if(previousChar == curChar && (new String(new byte[] {curChar}, CONTENT_ENCODING)).equals("\n")) {
                    break;
                }

                previousChar = curChar;

                buffer[bufferIndex] = curChar;
                bufferIndex++;
            }
                
            headers = new String(buffer, 0, bufferIndex, CONTENT_ENCODING);   
        } catch (Exception e) {
            try {
                sock.close();
            } catch (Exception f) {}
            return;
        }
        
        int contentLength = getContentLength(headers);
        
        String body = "";
            
        if(contentLength > MAXIMUM_BODY_SIZE) {
            // return request too large error
            try {
                sock.close();
            } catch (Exception f) {}
            return;
        }
            
        // now read the body (if len > 0)
        if(contentLength > 0) {
            try {
                buffer = new byte[contentLength];
                connInputStream.read(buffer, 0, contentLength);

                body = new String(buffer, CONTENT_ENCODING);
            } catch (Exception e) {
                // well, something went wrong
                try {
                    sock.close();
                } catch (Exception f) {}
                return;
            }
        }
        
        textResponse = headers + "\n" + body;
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
}
