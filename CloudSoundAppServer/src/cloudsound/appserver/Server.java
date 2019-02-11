/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver;

import java.net.*;
import java.io.IOException;

import cloudsound.common.SocketStore;
import cloudsound.common.ReceivingQueue;

/**
 *
 * @author oskaraugustyn
 */
public class Server {
    
    public Server(int port, String hostname) throws IOException {
        ServerSocket sock = new ServerSocket(port);

        //TODO: figure out a way to stop the server from the console.
        while(true) {
            String socketRef = SocketStore.put(sock.accept());
            ReceivingQueue.enqueue(socketRef);
        }
        
        //sock.close();
        
        // ALL validation performed in the processing threads
        /*String sockRef = SocketStore.put(conn);
        Message msg    = new Message(sockRef, )
        
        String sockName;
        
        while(SocketStore.size() < 4) {
        
            Socket conn = sock.accept();
            System.out.println("Accepted socket!");
            sockName = SocketStore.put(conn);
            
            System.out.println("Socket stored as: " + sockName + ".");
            System.out.println("There are now: " + SocketStore.size() + " active sockets.");
        
        }
        
        ConcurrentHashMap<String, Socket> map = SocketStore.getMap();*/
    }
    
    public Server(int port) throws IOException {
        this(port, "");
    }
    
}
