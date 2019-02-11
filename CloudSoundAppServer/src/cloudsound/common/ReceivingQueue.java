/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author UBlavins
 */
public class ReceivingQueue {
    private static LinkedBlockingQueue<String> QUEUE = new LinkedBlockingQueue();
    
    private ReceivingQueue() {
        
    }
    
    // this will put a socket into the queue
    public static void enqueue(String sock) {
        QUEUE.add(sock);
    }
    
    // this will get the oldest message in the queue
    public static String dequeue() {
        try {
            return QUEUE.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
    
    // same as dequeue but does not remove it
    public static String peek() {
        return QUEUE.peek();
    }
    
    public static boolean isEmpty() {
        return QUEUE.isEmpty();
    }
    
    public static LinkedBlockingQueue getQueueObject(){
        return QUEUE;
    }
}
