package com.tntrip;

public class HelloWorld {
    public static final byte[] ARRAY = new byte[1024 * 1024];

    public static void main(String[] args) {
        while (true) {
            System.out.println("Hello");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
