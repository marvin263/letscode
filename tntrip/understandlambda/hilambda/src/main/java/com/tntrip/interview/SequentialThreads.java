package com.tntrip.interview;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class SequentialThreads {
    private final Object monitor = new Object();
    private int threadCount;
    private boolean[] alreadyRun;

    public SequentialThreads(int threadCount) {
        this.threadCount = threadCount;
        alreadyRun = new boolean[threadCount];
    }

    public Thread[] createThreads() {
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread("" + i) {
                int idx = Integer.parseInt(this.getName());

                @Override
                public void run() {
                    synchronized (monitor) {
                        if (idx == 0) {
                            alreadyRun[idx] = true;
                            System.out.println(idx + " had run");
                            monitor.notifyAll();
                        } else {
                            // 前面一个尚未运行
                            while (!alreadyRun[idx - 1]) {
                                try {
                                    monitor.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Being here, 前面的肯定运行过了
                            alreadyRun[idx] = true;
                            System.out.println(idx + " had run");
                            monitor.notifyAll();
                        }
                    }
                }
            };
        }
        return threads;
    }


    public static void main(String[] args) {
        int threadCount = 21;

        Thread[] threads = new SequentialThreads(threadCount).createThreads();

        int[] threadIndices = createAndFillRandomValue(threadCount);

        for (int i = 0; i < threadCount; i++) {
            System.out.println("Start thread-" + threadIndices[i]);
            System.out.println();
            threads[threadIndices[i]].start();
        }
    }

    public static int[] createAndFillRandomValue(int arrayLength) {
        if (arrayLength <= 0) {
            return new int[0];
        }
        Random rdm = new Random();
        Set<Integer> existed = new LinkedHashSet<>();
        for (int i = 0; i < arrayLength; i++) {
            int v = rdm.nextInt(arrayLength);
            while (existed.contains(v)) {
                v = rdm.nextInt(arrayLength);
            }
            existed.add(v);
        }
        return existed.stream().mapToInt(e -> e).toArray();
    }
}
