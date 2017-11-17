package com.tntrip.interview;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by libing2 on 2016/10/9.
 */
public class PrintInOrderAnswer {
    private final Object lock = new Object();
    private Map<String, Boolean> shouldPrint = new HashMap<>();

    private Thread threadA = new Thread("thread-a") {
        @Override
        public void run() {
            printNowThenGoNext("A", "B");
        }
    };

    private Thread threadB = new Thread("thread-b") {
        @Override
        public void run() {
            printNowThenGoNext("B", "C");
        }
    };

    private Thread threadC = new Thread("thread-c") {
        @Override
        public void run() {
            printNowThenGoNext("C", null);
        }
    };

    private void printNowThenGoNext(final String strNow, final String strNext) {
        long begin = System.nanoTime();
        synchronized (lock) {
            while (!shouldPrint.get(strNow)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(strNow);
            if (strNext != null) {
                shouldPrint.put(strNext, true);
            }
            lock.notifyAll();
        }
        System.out.println(Thread.currentThread().getName() + " cost: " + (System.nanoTime() - begin));
    }

    public void startAll() {
        shouldPrint.put("A", true);
        shouldPrint.put("B", false);
        shouldPrint.put("C", false);
        List<Thread> threads = Arrays.asList(threadB, threadC, threadA);
        for (Thread t : threads) {
            t.start();
        }
    }

    public static void main(String[] args) {
        new PrintInOrderAnswer().startAll();
    }
}
