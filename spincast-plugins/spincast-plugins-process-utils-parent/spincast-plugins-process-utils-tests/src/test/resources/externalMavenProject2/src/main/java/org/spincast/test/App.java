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
        
        if(exitCode == 123) {
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                //...
            }
        }

        System.out.println("This is a System.out output1");
        System.err.println("This is a System.err output1");
        System.out.println("This is a System.out output2");
        System.err.println("This is a System.err output2");
 
        if(exitCode == 456) {
            try {
                Thread.sleep(60000);
            } catch (Exception ex) {
                //...
            }
        }
        
        System.exit(exitCode);
    }
}
