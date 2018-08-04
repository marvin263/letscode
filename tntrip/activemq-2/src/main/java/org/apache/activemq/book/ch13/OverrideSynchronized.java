package org.apache.activemq.book.ch13;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OverrideSynchronized {
    public interface A {
        void lockIt();

        void output();
    }

    public static class A1 implements A {
        volatile boolean stopOutput = false;

        public synchronized void lockIt() {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopOutput = true;
        }

        public synchronized void output() {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            for (int i = 0; i < 10; i++) {
                if (stopOutput) {
                    return;
                }
                System.out.println(sdf.format(new Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class A2 extends A1 implements A {
        public void output() {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            for (int i = 0; i < 10; i++) {
                if (stopOutput) {
                    return;
                }
                System.out.println(sdf.format(new Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class A3 extends A1 implements A {
        public void output() {
            super.output();
        }

    }

    public static class A4 extends A1 implements A {
        public synchronized void output() {
            super.output();
        }
    }

    public static class A5 extends A1 implements A {
        public synchronized void output() {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            for (int i = 0; i < 10; i++) {
                if (stopOutput) {
                    return;
                }
                System.out.println(sdf.format(new Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        A a = new A2();

        new Thread() {
            @Override
            public void run() {
                a.lockIt();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                a.output();
            }
        }.start();
    }
}
