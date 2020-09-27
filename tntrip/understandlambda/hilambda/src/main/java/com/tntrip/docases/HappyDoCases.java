package com.tntrip.docases;

import java.util.List;
import java.util.function.Supplier;

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
 *  -Xms1024m -Xmx1024m -XX:+HeapDumpBeforeFullGC -XX:+HeapDumpAfterFullGC -XX:HeapDumpPath=E:/dump/test.hdump
 *
 *  -XX:+PrintFlagsFinal -Xms1024m -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -Xloggc:E:/dump/mygc.log
 *
 * -XX:-UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=2 -Xms1024m -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps  -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -Xloggc:E:/dump/mygc.log
 *
 * </pre>
 */
public class HappyDoCases extends AbstractDoCases {
    public static final int M = 1024 * 1024;//1M

    public static <T> void keepLeftmostArrays(List<T> list, int expectedCount, Supplier<T> supplier) {
        int orgnSize = list.size();
        if (expectedCount == orgnSize) {
            System.out.println("expectedCount == orgnSize == " + expectedCount + ", do nothing");
            System.out.println();
            return;
        }
        if (expectedCount > orgnSize) {
            int addedCount = expectedCount - orgnSize;
            for (int i = 0; i < addedCount; i++) {
                list.add(supplier.get());
            }
            System.out.println(String.format("Created Objects, addedCount=%d, expectedCount=%d, orgnSize=%d", addedCount, expectedCount, orgnSize));
            System.out.println();
            return;
        }
        if (expectedCount < orgnSize) {
            for (int i = orgnSize - 1; i >= expectedCount; i--) {
                T rm = list.remove(i);
                if (rm instanceof Thread) {
                    ((Thread) rm).interrupt();
                }
            }
            System.out.println(String.format("Remove Objects, removedCount=%d, expectedCount=%d, orgnSize=%d", (orgnSize - expectedCount), expectedCount, orgnSize));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        AbstractDoCases.run(new HappyDoCases());
    }
}
