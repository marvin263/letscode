package com.tntrip.playzk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class TestWatcher {
    private static final Logger LOG = LoggerFactory.getLogger(TestWatcher.class);
    private static final String CONNECT_STRING = "172.31.30.14:2184";
    private static final int SESSION_TIMEOUT_IN_MILLS = 6000;

    private ZooKeeper zk;
    private String note;

    public class GlobalWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            LOG.info(note + " GlobalWatcher: " + event);
            String path = event.getPath();
            if (path != null) {
                LOG.info(note + " GlobalWatcher: path=" + path);

                try {
                    zk.getChildren(event.getPath(), true);
                    zk.getData(event.getPath(), true, new Stat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                LOG.info(note + " GlobalWatcher: path=" + path);
            }
        }
    }

    public class ExplicitWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            LOG.info(note + " ExplicitWatcher: " + event);

            String path = event.getPath();
            if (path != null) {
                LOG.info(note + " ExplicitWatcher: path=" + path);

                try {
                    zk.getChildren(event.getPath(), this);
                    zk.getData(event.getPath(), this, new Stat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                LOG.info(note + " ExplicitWatcher: path=" + path);
            }

        }
    }


    public TestWatcher(String note) throws Exception {
        this.note = note + "--" + getProcessID();
        this.zk = create(CONNECT_STRING, SESSION_TIMEOUT_IN_MILLS, new GlobalWatcher());
        setWatches(this.zk);
    }

    private ZooKeeper create(String connectString, int sessionTimeout, Watcher watcher) throws Exception {
        return new ZooKeeper(connectString, sessionTimeout, watcher);
    }

    private void setWatches(ZooKeeper zk) throws Exception {
        String str1 = "/watchIsTrue";
        zk.getChildren(str1, true);
        zk.getData(str1, true, new Stat());

        String str2 = "/explicitWatcher";
        ExplicitWatcher ew = new ExplicitWatcher();
        zk.getChildren(str2, ew);
        zk.getData(str2, ew, new Stat());
    }

    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TestWatcher tw1 = new TestWatcher("P1");
        Thread.sleep(1000);
        TestWatcher tw2 = new TestWatcher("P2");
        Thread.sleep(1000);
        TestWatcher tw3 = new TestWatcher("P3");

        final Object obj = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (obj) {

            }
        });
        t1.start();
        Thread.sleep(1000000000);
    }
}