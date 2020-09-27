package com.tntrip.docases;

import java.util.ArrayList;
import java.util.List;

public class CreateThreadCase implements EachCase {
    private List<Thread> createdThreads = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(createdThreads, expectedCount, () -> {
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            };
            t.start();
            return t;
        });
    }

    @Override
    public String[] prefix() {
        return new String[]{"t", "Creat Threads"};
    }
}
