/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver;

import cloudsound.common.SocketStore;
import cloudsound.common.Message;
import cloudsound.common.MessageQueue;
import cloudsound.common.Response;
import cloudsound.common.Router;
import cloudsound.common.ReceivingQueue;

import java.util.HashMap;
import java.net.Socket;

import java.io.OutputStream;

import java.nio.charset.Charset;

// NOTE: MessageDigest is NOT thread-safe, create a new object for each thread
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import java.util.Arrays;

/**
 *
 * @author oskaraugustyn
 */
public class ProcessingThread implements Runnable {

    private Thread t;
    private String threadName;
    
    public boolean activeLoop = true;
    
    public ProcessingThread(String name) {
        threadName = name;
    }
    
    @Override
    public void run() {
        while(activeLoop) {
            Message m = MessageQueue.dequeue();
            boolean keepAlive = m.header("Connection").equals("keep-alive");
            Response r;
            boolean checksumOk = true;
            
            // check if the checksum of the incoming message is correct.
            if(!m.header("Checksum").equals("")) {
                if(validateChecksum(m.body(), m.header("Checksum")) == false) {
                    // return error
                    checksumOk = false;
                    r = new Response(Response.CHECKSUM_VERIFICATION_FAILED);
                    r.setHeader("Date", String.valueOf((new java.util.Date()).getTime()));
                    // ack keep alive
                    if(keepAlive) {
                        r.setHeader("Connection", "keep-alive");
                    }
                    writeToClient(m.socketRef(), r.output());
                }
            }
            
            if(checksumOk) {
                try {
                    r = (Response) Router.call("cloudsound.appserver.routes." + m.controller(), m.action(), m);
                    r.setHeader("Date", String.valueOf((new java.util.Date()).getTime()));
                    if(r.body().length() > 0) {
                        r.setHeader("Content-Length", String.valueOf(r.body().getBytes("UTF-8").length));
                        r.setHeader("Cheksum", calculateChecksum(r.body(), "SHA-256"));
                    }
                    // ack keep alive
                    if(keepAlive) {
                        r.setHeader("Connection", "keep-alive");
                    }
                    writeToClient(m.socketRef(), r.output());
                } catch (NoSuchMethodException e) {
                    r = new Response(Response.INVALID_CONTROLLER_OR_ACTION);
                    r.setHeader("Date", String.valueOf((new java.util.Date()).getTime()));
                    // ack keep alive
                    if(keepAlive) {
                        r.setHeader("Connection", "keep-alive");
                    }
                    writeToClient(m.socketRef(), r.output());
                } catch (Exception e) {
                    e.printStackTrace();
                    r = new Response(Response.INTERNAL_SERVER_ERROR);
                    r.setHeader("Date", String.valueOf((new java.util.Date()).getTime()));
                    // ack keep alive
                    if(keepAlive) {
                        r.setHeader("Connection", "keep-alive");
                    }
                    writeToClient(m.socketRef(), r.output());
                }
            }
            
            if(keepAlive) {
                // if keep-alive request was sent, keep the socket open.
                ReceivingQueue.enqueue(m.socketRef());
            } else {
                try {
                   Socket conn = SocketStore.get(m.socketRef());
                   SocketStore.remove(m.socketRef());

                   conn.close();
                } catch (Exception e) {}
            }
        }
    }
    
    public boolean validateChecksum(String body, String checksum) {
        String[] checksumArray = checksum.split("/");
        String checksumType = "SHA-256";
        String[] allowableChecksumTypes = {"MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512"};
        
        if(checksumArray.length == 2) {
            if(Arrays.asList(allowableChecksumTypes).contains(checksumArray[0])) {
                checksumType = checksumArray[0];
            }
        }
        
        return checksumArray[1].equals(calculateChecksum(body, checksumType));
    }
    
    public String calculateChecksum(String body, String checksumType) {
        String[] allowableChecksumTypes = {"MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512"};
        String checksumType2 = "SHA-256";
        String checksum = "";
        
        if(Arrays.asList(allowableChecksumTypes).contains(checksumType)) {
            checksumType2 = checksumType;
        }
        
        // java hashing code based on: http://www.baeldung.com/java-md5
        try {
            MessageDigest md = MessageDigest.getInstance(checksumType2);
            md.update(body.getBytes());
            checksum = DatatypeConverter.printHexBinary(md.digest());
        } catch(Exception e) {
        
        }
        
        return checksumType2.toUpperCase() + "/" + checksum;
    }
   
    public void start() {
        if(t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    public void writeToClient(String socketRef, String text) {
        Socket conn = SocketStore.get(socketRef);
        
        OutputStream connOutputStream;
        try {
            connOutputStream = conn.getOutputStream();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        
        byte[] b = text.getBytes(Charset.forName("UTF-8"));
        
        try {
            connOutputStream.write(b);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        // close socket and remove socket from socket store.
    }
}
