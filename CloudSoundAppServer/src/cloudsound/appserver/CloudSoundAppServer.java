/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.appserver;

import cloudsound.common.Database;

/**
 *
 * @author UBlavins
 */
public class CloudSoundAppServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            Database.query("DELETE FROM active_user");
        } catch (Exception e) {
            System.out.println(e);
        } 
        
        ReceivingThread R1 = new ReceivingThread( "Thread-1");
        R1.start();
        
        ProcessingThread P1 = new ProcessingThread("Thread-2");
        P1.start();
        
        try {
            System.out.println("Starting server");
            new Server(9090);
        } catch (Exception e) {
            System.out.println("Exception starting server. Perhaps there is already a server running on this port?");
        }
    }
    
}
