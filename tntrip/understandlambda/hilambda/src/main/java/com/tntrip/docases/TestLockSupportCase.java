package com.tntrip.docases;

import com.tntrip.tidyfile.DateUtil;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class TestLockSupportCase implements EachCase {
    private Thread parkThread;

    public TestLockSupportCase() {
        parkThread = new Thread(() -> {
            AtomicInteger count = new AtomicInteger(0);
            while (true) {
                try {
                    LockSupport.park();
                    System.out.println(DateUtil.date2String(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS) +
                            ": park done, count= " + count.getAndIncrement() +
                            ", interrupted=" + Thread.currentThread().isInterrupted());
                    if (Thread.currentThread().isInterrupted()) {
                        Thread.interrupted();
                    }
                    System.out.println("After recover interrupt status, interrupted:" + Thread.currentThread().isInterrupted());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        parkThread.start();
    }

    @Override
    public void doOnLine(String line) {
        parkThread.interrupt();
    }

    @Override
    public String[] prefix() {
        return new String[]{"u", "park, unpark thread"};
    }
}
