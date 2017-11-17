package com.tntrip.understand.shutdownhook;

/**
 * Created by nuc on 2016/11/28.
 */
public class ShutdownHookExample {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Receiving signal-" + Thread.currentThread().getName());
                try {
                    Thread.sleep(5 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Done signal-" + Thread.currentThread().getName());
            }
        });
        while (true) {
            System.out.println("main-" + System.currentTimeMillis());
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
