package com.tntrip;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static String theInput = null;
    public static int printInputTimes = 0;

    public static void main(String[] args) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses
                .scheduleWithFixedDelay(TheadPoolEncounterBadThing::printTime, 500, 10000, TimeUnit.MILLISECONDS);
        //ses.scheduleWithFixedDelay(TheadPoolEncounterBadThing::printTheInput, 100, 4000, TimeUnit.MILLISECONDS);
        wait4YourInput();
    }

    public static void printTime() {
        if (!"e".equals(theInput)) {
            System.out.println(new SimpleDateFormat("yyyy-MM-DD hh:mm:ss").format(new Date()));
        } else {
            throw new RuntimeException("scheduled task throws exception");
        }
    }

    public static void printTheInput() {
        System.out.println((printInputTimes++) + " : " + theInput);
    }


    private static void wait4YourInput() {
        Scanner scn = new Scanner(System.in);
        while (true) {
            theInput = scn.nextLine();
        }
    }
}
