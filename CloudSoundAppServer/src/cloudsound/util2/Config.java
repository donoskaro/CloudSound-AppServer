/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.util2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author UBlavins
 */

public class Config {
    
    private static ConcurrentHashMap<String, String> CONFIG = new ConcurrentHashMap();
    
    private Config() {
        
    }
    
    public static void load() throws Exception {
        File inFile = new File("config.ini");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String keyVal[] = line.split("=");
            CONFIG.put((keyVal[0]).trim(), (keyVal[1]).trim());
        }
    }
    
    public static String get(String value) {
        if (CONFIG.containsKey(value)) {
            return CONFIG.get(value);
        }
        return "";
    }
    
}
