package com.tntrip.understand.knowfinal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StuffIntoPublic
 * <p>
 * Unsafe publication
 *
 * @author Brian Goetz and Tim Peierls
 */
public class StuffIntoPublic {
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static class AssignmentTask implements Runnable {
        private StuffIntoPublic sip;

        public AssignmentTask(StuffIntoPublic sip) {
            this.sip = sip;
        }

        @Override
        public void run() {
            ExecutorService es = Executors.newFixedThreadPool(200);
            es.submit((Runnable) () -> {
                while (true) {
                    sip.initialize();
                }
            });
        }
    }

    public static class AssertTask implements Runnable {
        private StuffIntoPublic sip;

        public AssertTask(StuffIntoPublic sip) {
            this.sip = sip;
        }

        @Override
        public void run() {
            while (true) {
                sip.holder.assertSanity();// 会抛出异常
            }
        }
    }

    public static void main(String[] args) throws Exception {
        StuffIntoPublic sip = new StuffIntoPublic();
        Thread assertThread = new Thread(new AssertTask(sip));
        Thread assignmentThread = new Thread(new AssignmentTask(sip));
        assignmentThread.start();
        Thread.sleep(1000);
        assertThread.start();
    }
}
