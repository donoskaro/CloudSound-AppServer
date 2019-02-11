/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common.util;

import java.util.Random;

/**
 *
 * @author UBlavins
 */
public class Rand {
    
    private Rand() {
        
    }
    
    //NOTE: code for this method obtained from: https://stackoverflow.com/a/20536597
    public static String alphanumeric(int len) {
        if (len < 1) {
            len = 16;
        }
        String pool = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuffer output = new StringBuffer();
        Random randm = new Random();
        while (output.length() < len) {
            int index = (int) (randm.nextFloat() * pool.length());
            output.append(pool.charAt(index));
        }
        return output.toString();
    }
    
}
