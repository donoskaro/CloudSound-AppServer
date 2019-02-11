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
public class MessageQueue {
    
    private static LinkedBlockingQueue<Message> QUEUE = new LinkedBlockingQueue();
    
    public MessageQueue() {
        
    }
    
    // this will put a message into the queue
    public static void enqueue(Message msg) {
        QUEUE.add(msg);
    }
    
    // this will get the oldest message in the queue
    public static Message dequeue() {
        try {
            return QUEUE.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
    
    // same as dequeue but does not remove it
    public static Message peek() {
        return QUEUE.peek();
    }
    
    public static boolean isEmpty() {
        return QUEUE.isEmpty();
    }
    
    public static LinkedBlockingQueue getQueueObject(){
        return QUEUE;
    }
    
}
