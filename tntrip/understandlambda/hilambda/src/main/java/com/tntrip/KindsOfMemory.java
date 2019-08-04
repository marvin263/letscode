package com.tntrip;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
 * </pre>
 */
public class KindsOfMemory extends MinMaxFreeHeapRatio {
    public static final int M = 1024 * 1024;//1M
    private List<byte[]> heapMemory = new ArrayList<>();
    private List<ByteBuffer> directMemory = new ArrayList<>();
    private List<Class<?>> metaspaceMemory = new ArrayList<>();
    private List<MappedByteBuffer> mmapMemory = new ArrayList<>();
    private List<String> internedString = new ArrayList<>();
    private List<Thread> createdThreads = new ArrayList<>();


    public class CreateThreadCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(createdThreads, expectedCount, () -> {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
                return t;
            });
        }

        @Override
        public String[] prefix() {
            return new String[]{"t", "Creat Threads"};
        }
    }

    public class AllocateInternedStringCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(internedString, expectedCount, () -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 1024; i++) {
                    sb.append(UUID.randomUUID().toString());
                }
                return sb.toString().intern();
            });
        }

        @Override
        public String[] prefix() {
            return new String[]{"str", "Intern String"};
        }
    }

    public class AllocateMmapCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(mmapMemory, expectedCount, () -> {
                File f = new File("C:\\Users\\libin\\Downloads\\VMware-workstation-full-14.1.3-9474260.exe");
                long length = f.length();
                try {
                    RandomAccessFile raf = new RandomAccessFile(f, "rw");
                    FileChannel channel = raf.getChannel();
                    MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_WRITE,
                            new Random().nextInt((int) length / 100),
                            new Random().nextInt((int) (length - (length * 0.05))));
                    return buf;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            });
        }

        @Override
        public String[] prefix() {
            return new String[]{"mm", "Allocate mmapped memory"};
        }
    }


    public class AllocateMetaspaceCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(metaspaceMemory, expectedCount, () -> {
                MyClassLoader m = new MyClassLoader("D:\\eden\\gitworkspace\\letscode\\tntrip\\understandlambda\\hilambda\\build\\classes\\main\\");
                Class c = m.findClass("com.tntrip.HelloWorld");
                return c;
            });
        }

        @Override
        public String[] prefix() {
            return new String[]{"mt", "Allocate metaspace"};
        }
    }

    public class AllocateHeapCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(heapMemory, expectedCount, () -> new byte[M]);
        }

        @Override
        public String[] prefix() {
            return new String[]{"h", "Allocate heap space"};
        }
    }

    public class AllocateDirectMemoryCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(directMemory, expectedCount, () -> ByteBuffer.allocateDirect(M));
        }

        @Override
        public String[] prefix() {
            return new String[]{"dm", "Allocate direct memory"};
        }
    }

    private <T> void keepLeftmostArrays(List<T> list, int expectedCount, Supplier<T> supplier) {
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
                list.remove(i);
            }
            System.out.println(String.format("Remove Objects, removedCount=%d, expectedCount=%d, orgnSize=%d", (orgnSize - expectedCount), expectedCount, orgnSize));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        AbstractDoCases.run(new KindsOfMemory());
    }
}
