package org.apache.http.examples.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by libing2 on 2015/9/19.
 */
public class NamedThreadFactory implements ThreadFactory {
    private AtomicInteger ac = new AtomicInteger(0);
    private String namePrefix;

    public NamedThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + "-" + ac.getAndIncrement());
        return t;
    }
}