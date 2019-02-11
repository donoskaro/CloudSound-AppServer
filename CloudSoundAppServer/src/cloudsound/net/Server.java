/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.net;

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
    }
    
    public Server(int port) throws IOException {
        this(port, "");
    }
    
}
