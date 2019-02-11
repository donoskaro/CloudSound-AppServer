/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.management.Query;

/**
 *
 * @author UBlavins
 */
public class Database {
    
    // Database.fetch(String query) returns HashMap of rows 
    // Database.query(String query) execute query
    
    private static final String dbPath = "jdbc:sqlite:database.db";
    private static HashMap<Integer, HashMap<String, String>> rows;
    
    private Database() {
        
    }
    
    public static HashMap<Integer, HashMap<String, String>> fetch(String Query) throws SQLException {
        rows = new HashMap();
        Connection dbConnection = DriverManager.getConnection(dbPath);
        Statement statement = dbConnection.createStatement();
        ResultSet result = statement.executeQuery(Query);
        // This should be for the headers.
        ResultSetMetaData metadata = result.getMetaData();
        Integer rowNum = 0;
        while (result.next()) {
            HashMap<String, String> columnValue = new HashMap();
            // no reference to getColumnName(0)
            for (Integer i = 1; i <= metadata.getColumnCount(); i++) {
                columnValue.put(metadata.getColumnName(i),result.getString(i));
            }
            rows.put(rowNum, columnValue);
            rowNum++;
        }
        return rows;
    }
    
    public static void query(String Query) throws SQLException {
        Connection dbConnection = DriverManager.getConnection(dbPath);
        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(Query);
    }
    
}
