package com.tntrip.playzk;

/**
 * A simple example program to use DataMonitor to start and
 * stop executables based on a znode. The program watches the
 * specified znode and saves the data that corresponds to the
 * znode in the filesystem. It also starts the specified program
 * with the specified arguments when the znode exists and kills
 * the program if the znode goes away.
 */

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestWatcher implements Watcher {
    private static final Logger LOG = LoggerFactory.getLogger(TestWatcher.class);
    public static final String CONNECT_STRING = "192.168.86.149:2181,192.168.86.149:2182,192.168.86.149:2183";
    private ZooKeeper zk;


    public TestWatcher() throws IOException {
        zk = new ZooKeeper(CONNECT_STRING, 6000, this);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Logger LOG = LoggerFactory.getLogger(TestWatcher.class);
        LOG.info("ddddd");
        TestWatcher tw = new TestWatcher();
        synchronized (tw){
            tw.wait();
        }
    }

    /***************************************************************************
     * We do process any events ourselves, we just need to forward them on.
     *
     * @see Watcher#process(WatchedEvent)
     */
    public void process(WatchedEvent event) {
        System.out.println(zk.getClientConfig());
        ZKClientConfig cc = zk.getClientConfig();
        
        System.out.println(event);
    }
}