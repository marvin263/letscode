package com.tntrip;

public class ThreadLocalTest {
    public static final ThreadLocal<String> itl = new InheritableThreadLocal<>();

    public static void main(String[] args) throws Exception {
        itl.set(Thread.currentThread().getName());
        System.out.println(itl.get());
        Thread t = new Thread("second-thread") {
            @Override
            public void run() {
                System.out.println(itl.get());
            }
        };
        t.start();
        Thread.sleep(200000);
    }


}
