/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsound.common;
import java.lang.reflect.Method;

import cloudsound.common.Response;
import cloudsound.common.Message;

/**
 *
 * @author oskaraugustyn
 */
public class Router {
    
    private Router() {}
    
    // return Object should be return Response. Not sure what went wrong.
    public static Object call(String controller, String action, Message message) throws NoSuchMethodException, Exception {
        if(routeExists(controller, action) == false) {
            throw new NoSuchMethodException();
        }
        
        // used: https://stackoverflow.com/questions/10876552/how-to-check-if-a-java-class-has-a-particular-method-in-it
        Method m;
        Class c;
        try {
            m = getMethod(controller, action);
            c = getClass(controller);
        }
        catch (Exception e) {
            throw new NoSuchMethodException();
        }
        
        Object o;
        try {
            o = c.getConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception();
        }
        
        //return m.invoke(o, invoke(this, message));
        return m.invoke(o, message);
    }
    
    public static boolean routeExists(String controller, String action) {
        return hasMethod(controller, action);
    }
    
    private static Method getMethod(String className, String methodName) throws NoSuchMethodException {
        if(hasMethod(className, methodName) == false) {
            throw new NoSuchMethodException();
        }
        
        try {
            return getClass(className).getMethod(methodName, Message.class);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new NoSuchMethodException();
        }
    }
    
    // code from: https://stackoverflow.com/questions/10876552/how-to-check-if-a-java-class-has-a-particular-method-in-it
    private static boolean hasMethod(String className, String methodName) {
        try {
            getClass(className).getMethod(methodName, Message.class);
            return true;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            return false;
        }
    }
    
    private static Class getClass(String name) throws ClassNotFoundException {
        if(isClass(name) == false) {
            throw new ClassNotFoundException();
        }
        
        Class c = Class.forName(name);
        return c;
    }
    
    private static boolean isClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}