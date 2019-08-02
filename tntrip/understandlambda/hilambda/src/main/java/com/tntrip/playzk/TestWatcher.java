package com.tntrip.playzk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicLong;

public class TestWatcher {
    private static final Logger LOG = LoggerFactory.getLogger(TestWatcher.class);
    private static final String CONNECT_STRING = "192.168.86.158:2181,192.168.86.158:2182,192.168.86.158:2183";
    private static final int SESSION_TIMEOUT_IN_MILLS = 30000;

    private ZooKeeper zk;
    private String note;
    private static AtomicLong ROUND = new AtomicLong(0);
    private AtomicLong global = new AtomicLong(0);
    private AtomicLong explicit = new AtomicLong(0);

    public class GlobalWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            String info = assembleInfo(global);
            LOG.info(info + " GlobalWatcher: " + event);
            String path = event.getPath();
            if (path != null && event.getType() != Event.EventType.NodeDeleted) {
                try {
                    zk.getChildren(event.getPath(), true);
                    zk.getData(event.getPath(), true, new Stat());
                    zk.exists("/watchIsTrue/w2", true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            if (event.getState() == Event.KeeperState.Expired) {
                LOG.info("GlobalWatcher RECEIVED Event.KeeperState.Expired");
                try {
                    ROUND.incrementAndGet();
                    zk = create(CONNECT_STRING, SESSION_TIMEOUT_IN_MILLS, new GlobalWatcher());
                    setWatches(zk);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String assembleInfo(AtomicLong globalOrExplicit) {
        return "Round--" + ROUND.get() + ", " + note + "~~" + globalOrExplicit.getAndIncrement();
    }

    public class ExplicitWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            String info = assembleInfo(explicit);
            LOG.info(info + " ExplicitWatcher: " + event);

            String path = event.getPath();
            if (path != null && event.getType() != Event.EventType.NodeDeleted) {
                try {
                    zk.getChildren(event.getPath(), this);
                    zk.getData(event.getPath(), this, new Stat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (event.getState() == Event.KeeperState.Expired) {
                LOG.info("ExplicitWatcher RECEIVED Event.KeeperState.Expired");
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
        ExplicitWatcher ew = new ExplicitWatcher();
        String str2 = "/explicitWatcher";
        zk.getChildren(str2, ew);
        zk.getData(str2, ew, new Stat());

        String str1 = "/watchIsTrue";
        zk.getChildren(str1, true);
        zk.getData(str1, true, new Stat());
        zk.getData(str1 + "/w1", true, new Stat());
        zk.exists(str1 + "/w2", true);
    }

    public static int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TestWatcher tw1 = new TestWatcher(args[0]);
        Thread.sleep(1000000000);
    }
}