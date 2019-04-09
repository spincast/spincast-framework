package org.spincast.test;

public class App {
    
    public static void main(String[] args) {
        int exitCode = Integer.parseInt(args[0]);
        
        if(exitCode == 100) {
            throw new RuntimeException("Some Exception");
        }
        
        if(exitCode == 1000) {
            return;
        }
        
        System.exit(exitCode);
    }

}
