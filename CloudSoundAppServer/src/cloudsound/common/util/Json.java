/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.JsonElement;

/**
 *
 * @author oskar
 */
public class Json {
    
    private JsonObject o;
    
    public Json(String text) throws Exception {
        try {
            o = new JsonParser().parse(text).getAsJsonObject();
        } catch(Exception e) {
            throw new Exception();
        }
    }
    
    public String getAsString(String key) {
        try {
            return o.get(key).getAsString();
        } catch(Exception e) {
            return "";
        }
    }

}
