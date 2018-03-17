package com.tntrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nuc on 2018/3/5.
 * <p>
 * <pre>
 *  -Xms10M -Xmx400M -XX:MinHeapFreeRatio=30 -XX:MaxHeapFreeRatio=70 -XX:+PrintGCDetails -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime  -XX:-DisableExplicitGC -XX:MaxTenuringThreshold=6 -XX:OldPLABSize=16  -XX:+UseCompressedClassPointers -XX:+UseCompressedOops  -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 *  -Xms10M -Xmx400M -XX:+PrintGCDetails -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 *  -XX:+UseSerialGC -Xms10M -Xmx400M -XX:+PrintGCDetails -Xloggc:c:/gc/gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:/gc/heapdump.hprof
 *
 * </pre>
 */
public class TheadPoolEncounterBadThing {

    private List<byte[]> listArray = new ArrayList<>();
    public static final int ARRAY_SIZE = 1024 * 1024;//1M
    public static String theInput = null;

    public static void main(String[] args) {
        TheadPoolEncounterBadThing mmfhr = new TheadPoolEncounterBadThing();
        ScheduledExecutorService sss = Executors.newSingleThreadScheduledExecutor();
        sss.scheduleWithFixedDelay(TheadPoolEncounterBadThing::doSth, 500, 10000, TimeUnit.MILLISECONDS);
        mmfhr.fdfd();
    }

    public static void doSth() {
        if (!"exception".equals(theInput)) {
            System.out.println(new SimpleDateFormat("yyyy-MM-DD hh:mm:ss").format(new Date()) + " -- " + theInput);
        }else{
            throw new RuntimeException("SSSSSS");
        }
    }

    private static void fdfd() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            theInput = scn.nextLine();
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
