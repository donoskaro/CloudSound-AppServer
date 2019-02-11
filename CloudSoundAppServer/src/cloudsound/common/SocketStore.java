/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common;

import java.util.concurrent.ConcurrentHashMap;
import java.net.Socket;

import cloudsound.common.util.Rand;

/**
 *
 * @author oskaraugustyn
 */
public class SocketStore {
    
    private static ConcurrentHashMap<String, Socket> STORE = new ConcurrentHashMap();
    private static int DEFAULT_KEY_LENGTH = 32;
    
    // making the constructor private: static class
    private SocketStore() {
        
    }
    
    // STORE has it's own put method
    public static String put(Socket sock, String name) {
        STORE.put(name, sock);
        return name;
    }
    
    // store with a random alphnumeric name, return name
    // possible bug: might not be thread safe
    public static String put(Socket sock) {
        // checks if name exists, if yes: re-generate until empty slot found
        String name = Rand.alphanumeric(DEFAULT_KEY_LENGTH);
        
        while(STORE.containsKey(name)) {
            name = Rand.alphanumeric(DEFAULT_KEY_LENGTH);
        }
        
        return put(sock, name);
    }
    
    public static Socket get(String name) {
        return STORE.get(name);
    }
    
    public static int size() {
        return STORE.size();
    }
    
    public static ConcurrentHashMap getMap() {
        return STORE;
    }
    
    public static void remove(String socket) {
        if (STORE.containsKey(socket)) {
            STORE.remove(socket);
        }
    }
}
