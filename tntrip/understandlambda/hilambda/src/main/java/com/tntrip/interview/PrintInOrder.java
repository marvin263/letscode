package com.tntrip.interview;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * 问题：让三个线程 按顺序 分别输出 A，B，C
 *
 * 要求：
 * 1. A, B, C 必须由不同的线程输出
 * 2. 无论 线程的启动顺序 如何，都要保证 先输出A、再输出B，再输出C
 * 3. 不要通过 “让线程sleep不同的时间段” 来实现。
 *     即不要通过：threadC.sleep(5000), threadB.sleep(3000), threadA.sleep(1000) 或 mainThread.sleep(xxx)来实现
 *
 * 你可以 在任意地方，加任意代码，只要实现上述效果即可
 *
 * </pre>
 */
public class PrintInOrder {
    private Thread threadA = new Thread("thread-a") {
        @Override
        public void run() {
        }
    };

    private Thread threadB = new Thread("thread-b") {
        @Override
        public void run() {
        }
    };

    private Thread threadC = new Thread("thread-c") {
        @Override
        public void run() {
        }
    };

    public void startAll() {
        List<Thread> threads = Arrays.asList(threadB, threadC, threadA);
        for (Thread t : threads) {
            t.start();
        }
    }

    public static void main(String[] args) {
        new PrintInOrder().startAll();
    }
}
