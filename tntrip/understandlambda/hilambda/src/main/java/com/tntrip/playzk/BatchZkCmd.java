package com.tntrip.playzk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.Scanner;

/**
 * Created by nuc on 2018/3/5.
 *
 * <pre>
 *  -Xms10M -Xmx400M -XX:MinHeapFreeRatio=30 -XX:MaxHeapFreeRatio=70 -XX:+PrintGCDetails -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime  -XX:-DisableExplicitGC -XX:MaxTenuringThreshold=6 -XX:OldPLABSize=16  -XX:+UseCompressedClassPointers -XX:+UseCompressedOops  -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 *  -Xms10M -Xmx400M -XX:+PrintGCDetails -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 *  -XX:+UseSerialGC -Xms10M -Xmx400M -XX:+PrintGCDetails -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 * </pre>
 */
public class BatchZkCmd {
    private static final String CONNECT_STRING = "192.168.86.158:2181,192.168.86.158:2182,192.168.86.158:2183";
    private static final int SESSION_TIMEOUT_IN_MILLS = 30000;
    private ZooKeeper zk;

    public BatchZkCmd() {
        try {
            zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT_IN_MILLS, event -> {
                System.out.println(event.toString());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        BatchZkCmd mmfhr = new BatchZkCmd();
        mmfhr.fdfd();
    }

    private void fdfd() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            String line = scn.nextLine();
            String[] splits = line.split("\\s");
            String cmd = splits[0];
            if ("create".equals(cmd)) {
                for (int i = 1; i < splits.length; i++) {
                    String s = splits[i];
                    if (!"".equals(s.trim())) {
                        try {
                            zk.create(s, s.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                            System.out.println("Done: " + cmd + " " + s);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                System.out.println();
                System.out.println("Done: " + line);
                continue;
            }
            if ("delete".equals(cmd) || "deleteall".equals(cmd)) {
                for (int i = 1; i < splits.length; i++) {
                    String s = splits[i];
                    if (!"".equals(s.trim())) {
                        try {
                            zk.delete(s, -1);
                            System.out.println("Done: " + cmd + " " + s);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                System.out.println();
                System.out.println("Done: " + line);
                continue;
            }
            if ("set".equals(cmd)) {
                for (int i = 1; i < splits.length; i++) {
                    String s = splits[i];
                    if (!"".equals(s.trim())) {
                        String[] pathData = s.split(":");
                        try {
                            zk.setData(pathData[0], pathData[1].getBytes(), -1);
                            System.out.println("Done: " + cmd + " " + s);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                System.out.println("Done: " + line);
                System.out.println();
                continue;
            }
        }
    }
}
