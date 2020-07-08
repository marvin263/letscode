package com.tntrip;

import com.tntrip.playzk.Executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalTest {
    public static final ThreadLocal<String> itl = new InheritableThreadLocal<>();

    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(3);
        es.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("dddddd");
            }
        });
        es.awaitTermination(20, TimeUnit.HOURS);
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

