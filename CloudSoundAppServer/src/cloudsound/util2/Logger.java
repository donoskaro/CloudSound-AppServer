/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.util2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author UBlavins
 */
public class Logger {
    
    /**
     * Log Levels:
	- Debug (minimum level)
	- Info
	- Notice
	- Warning
	- Error (maximum level)

Log levels available as methods (i.e. logger.warning("A warning");)

Log methods should accept a string.

Log output should be in the following format:
[Level] 01/02/18 12:34 Log Message
e.g.
[Error] 21/03/18 11:15 Could not open the map file.

Constructor should have the following parameters:
- string name: the name of the log file.
- string minLogLevel: the minimum log level to be written down to the file. e.g. if the value given is: "notice" then anything with that level or higher (warning, error) will be written down to the file. Anything else will be discarded.

Example constructor:
Logger logger = new Logger("a/p/p.log", "warning");
     */
    
    private static int MAX_LEVEL = 5;
    private static int DEFAULT   = 4;
    private static String[] LOG_LEVELS = new String[] {"DEBUG","INFO","NOTICE","WARNING","ERROR"};
    private String filename;
    private int minLogLevel;
    
    // IMPLEMENT: Print to console
    public Logger(String fileName, String minLevelLog) {
        filename = fileName;
        minLogLevel = index(minLevelLog);
    }
    
    public void log(String logLevel, String logMessage) throws IOException {
        BufferedWriter toLog = new BufferedWriter(new FileWriter(filename, true));
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");  
        LocalDateTime now = LocalDateTime.now();   
        if (index(logLevel) >= minLogLevel) {
            toLog.append("[" + logLevel + "] " + date.format(now) + " " + logMessage);
        }
    }
    
    public void debug(String message) throws IOException {
        log(LOG_LEVELS[0], message);
    }
    
    public void info(String message) throws IOException {
        log(LOG_LEVELS[1], message);
    }
    
    public void notice(String message) throws IOException {
        log(LOG_LEVELS[2], message);
    }
    
    public void warning(String message) throws IOException{
        log(LOG_LEVELS[3], message);
    }
    
    public void error(String message) throws IOException{
        log(LOG_LEVELS[4], message);
    }
    
    private int index(String logLevel) {
        for (int i = 0; i < MAX_LEVEL; i++) {
            if (logLevel.equals(LOG_LEVELS[i])) {
                return i;
            }
        }
        return 0;
    }
    
}