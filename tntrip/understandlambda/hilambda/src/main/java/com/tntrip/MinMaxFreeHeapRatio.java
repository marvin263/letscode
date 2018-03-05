package com.tntrip;

import java.util.ArrayList;
import java.util.List;
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
 *
 */
public class MinMaxFreeHeapRatio {

    private List<byte[]> listArray = new ArrayList<>();
    public static final int ARRAY_SIZE = 1024 * 1024;//1M

    public static void main(String[] args) {
        MinMaxFreeHeapRatio mmfhr = new MinMaxFreeHeapRatio();
        mmfhr.fdfd();
    }

    private void fdfd() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            String line = scn.nextLine();
            if ("quit".equals(line)) {
                System.out.println("quit");
                System.out.println();
                break;
            }
            if ("gc".equals(line)) {
                System.gc();
                System.out.println("gc done");
                System.out.println();
                continue;
            }
            int expectedCount = Integer.valueOf(line);
            keepLeftmostArrays(expectedCount);
        }
    }

    private void keepLeftmostArrays(int expectedCount) {
        int orgnSize = listArray.size();
        if (expectedCount == orgnSize) {
            System.out.println("expectedCount == orgnSize == " + expectedCount + ", do nothing");
            System.out.println();
            return;
        }
        if (expectedCount > orgnSize) {
            int addedCount = expectedCount - orgnSize;
            for (int i = 0; i < addedCount; i++) {
                listArray.add(new byte[ARRAY_SIZE]);
            }
            System.out.println(String.format("Created Objects, addedCount=%d, expectedCount=%d, orgnSize=%d", addedCount, expectedCount, orgnSize));
            System.out.println();
            return;
        }
        if (expectedCount < orgnSize) {
            for (int i = orgnSize - 1; i >= expectedCount; i--) {
                listArray.remove(i);
            }
            System.out.println(String.format("Remove Objects, removedCount=%d, expectedCount=%d, orgnSize=%d", (orgnSize - expectedCount), expectedCount, orgnSize));
            System.out.println();
            return;
        }
    }

}
