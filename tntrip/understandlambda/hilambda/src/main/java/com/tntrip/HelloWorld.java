package com.tntrip;

public class HelloWorld {
    public static void main(String[] args) {
        while(true){
            System.out.println("Hello");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
