package com.tntrip.playzk;

import com.tntrip.tidyfile.TnStringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class TestWatcher {
    private static final Logger LOG = LoggerFactory.getLogger(TestWatcher.class);
    private static final String CONNECT_STRING = "192.168.86.203:2181,192.168.86.204:2181,192.168.86.205:2181,192.168.86.206:2181,192.168.86.207:2181";
    //private static final String CONNECT_STRING = "192.168.86.203:2181";

    // minSessionTimeout 和 maxSessionTimeout，单位ms
    // 默认为2*tickTime和20*tickTime
    private static final int SESSION_TIMEOUT_IN_MILLS = 40000 * 5;

    private ZooKeeper zk;
    private int processId;
    private static volatile AtomicLong ZKROUND = new AtomicLong(0);

    private static volatile AtomicLong EXPIRE = new AtomicLong(0);
    private static volatile AtomicLong GLOBAL = new AtomicLong(0);
    private static volatile AtomicLong EXPLICIT = new AtomicLong(0);

    public TestWatcher() throws Exception {
        processId = TnStringUtils.getProcessID();
        createZK();
    }

    private void createZK() throws Exception {
        System.out.println("Start Process: " + processId);
        if (zk != null) {
            zk.close(10 * 1000);
        }
        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT_IN_MILLS, new GlobalWatcher());
        System.out.println("Create new ZooKeeper instance. ZKROUND=" + ZKROUND.incrementAndGet());

        EXPIRE.set(0L);
        GLOBAL.set(0L);
        EXPLICIT.set(0L);
        setWatches(zk);
        System.out.println(readableContext());
    }

    private void setWatches(ZooKeeper zk) throws Exception {
        zk.exists("/dummy", new ExpireWatcher());

        ExplicitWatcher ew = new ExplicitWatcher();
        String str2 = "/explicitWatcher";

        long b1 = System.currentTimeMillis();
        System.out.println("explicitWatcher begin");
        if (zk.exists(str2, false) == null) {
            zk.create(str2, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        System.out.println("explicitWatcher end, cost: " + (System.currentTimeMillis() - b1));

        zk.getChildren(str2, ew);
        zk.getData(str2, ew, new Stat());

        String str1 = "/globalWatcher";
        if (zk.exists(str1, false) == null) {
            zk.create(str1, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        long b2 = System.currentTimeMillis();
        System.out.println("globalWatcher begin");
        zk.getChildren(str1, true);
        System.out.println("globalWatcher end, cost: " + (System.currentTimeMillis() - b2));
        zk.getData(str1, true, new Stat());
    }

    public class GlobalWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            GLOBAL.incrementAndGet();
            LOG.info("GlobalWatcher: " + event + ". " + readableContext());
            // path上能设置事件，那就设置上事件 
            String path = event.getPath();
            if (path != null && event.getType() != Event.EventType.NodeDeleted) {
                try {
                    zk.getChildren(path, true);
                    zk.getData(path, true, new Stat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class ExpireWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            EXPIRE.incrementAndGet();
            LOG.info("ExpireWatcher: " + event + ". " + readableContext());
            // expire就得创建 ZooKeeper实例
            if (event.getState() == Event.KeeperState.Expired) {
                try {
                    createZK();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String readableContext() {
        return TnStringUtils.format("Process--{0}, zkRound={1}, sessionId={2}, Expire={3}, Global={4}, Explicit={5}",
                processId + "",
                ZKROUND.get() + "",
                zk.getSessionId() + "",
                EXPIRE.get() + "",
                GLOBAL.get() + "",
                EXPLICIT.get() + "");
    }

    public class ExplicitWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            EXPLICIT.incrementAndGet();
            LOG.info("ExplicitWatcher: " + event + ". " + readableContext());
            // path上能设置事件，那就设置上事件 
            String path = event.getPath();
            if (path != null && event.getType() != Event.EventType.NodeDeleted) {
                try {
                    zk.getChildren(event.getPath(), this);
                    zk.getData(event.getPath(), this, new Stat());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TestWatcher tw1 = new TestWatcher();
        Thread.sleep(1000000000);
    }
}