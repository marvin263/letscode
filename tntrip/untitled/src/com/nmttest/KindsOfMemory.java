package com.nmttest;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


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
public class KindsOfMemory extends AbstractDoCases {
    public static final int M = 1024 * 1024;//1M
    private List<byte[]> heapMemory = new ArrayList<>();
    private List<ByteBuffer> directMemory = new ArrayList<>();
    private List<Class<?>> metaspaceMemory = new ArrayList<>();
    private List<MappedByteBuffer> mmapMemory = new ArrayList<>();
    private List<String> internedString = new ArrayList<>();
    private List<Thread> createdThreads = new ArrayList<>();

    public class GetJavaVersion implements EachCase {
        @Override
        public void doOnLine(String line) {
            System.out.println("Java version: " + System.getProperty("java.version")
                    + "\ndata model:" + System.getProperty("sun.arch.data.model"));
            System.out.println();
        }

        @Override
        public String[] prefix() {
            return new String[]{"v", "Get java version and data model"};
        }
    }

    public class GetPidCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            System.out.println(runtimeMXBean.getName().split("@")[0]);
            System.out.println();
        }

        @Override
        public String[] prefix() {
            return new String[]{"p", "Get pid"};
        }
    }

    public class CreateThreadCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(createdThreads, expectedCount, new Supplier<Thread>() {
                @Override
                public Thread get() {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                        }
                    };
                    t.start();
                    return t;
                }
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
            keepLeftmostArrays(internedString, expectedCount, new Supplier<String>() {
                @Override
                public String get() {
                    StringBuilder sb = new StringBuilder();
                    while (sb.length() < (M / 2)) {
                        sb.append(UUID.randomUUID().toString());
                    }
                    return sb.toString().intern();
                }
            });
        }

        @Override
        public String[] prefix() {
            return new String[]{"str", "Intern String"};
        }
    }

    public class AllocateMmapCase implements EachCase {
        public static final String MM_FILE_NAME = "bigfile.data";

        @Override
        public void doOnLine(String line) {
            final String mmFile = Thread.currentThread().getContextClassLoader().getResource("").getPath() + MM_FILE_NAME;
            int expectedCount = Integer.valueOf(line.substring(prefix()[0].length()));
            keepLeftmostArrays(mmapMemory, expectedCount, new Supplier<MappedByteBuffer>() {
                @Override
                public MappedByteBuffer get() {
                    File f = new File(mmFile);
                    long length = f.length();
                    try {
                        RandomAccessFile raf = new RandomAccessFile(f, "rw");
                        FileChannel channel = raf.getChannel();
                        int size = new Random().nextInt((int) (length - (length * 0.05)));
                        MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_WRITE,
                                new Random().nextInt((int) length / 100),
                                size);
                        char aChar = buf.getChar();
                        System.out.println(aChar);
                        return buf;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
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
            keepLeftmostArrays(metaspaceMemory, expectedCount, new Supplier<Class<?>>() {
                @Override
                public Class<?> get() {
                    return new MyClassLoader().findClass("com.nmttest.HelloWorld");
                }
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
            keepLeftmostArrays(heapMemory, expectedCount, new Supplier<byte[]>() {
                @Override
                public byte[] get() {
                    return new byte[M];
                }
            });
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
            keepLeftmostArrays(directMemory, expectedCount, new Supplier<ByteBuffer>() {
                @Override
                public ByteBuffer get() {
                    return ByteBuffer.allocateDirect(M);
                }
            });
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
        AbstractDoCases.run(new KindsOfMemory());
    }
}
