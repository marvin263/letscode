package com.tntrip;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.tntrip.get24.Get24;
import com.tntrip.tidyfile.DateUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
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
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
            keepLeftmostArrays(createdThreads, expectedCount, () -> {
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
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
            keepLeftmostArrays(internedString, expectedCount, () -> {
                StringBuilder sb = new StringBuilder();
                while (sb.length() < (M / 2)) {
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
        public static final String MM_FILE_NAME = "bigfile.data";

        @Override
        public void doOnLine(String line) {
            String mmFile = Thread.currentThread().getContextClassLoader().getResource("").getPath() + MM_FILE_NAME;
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
            keepLeftmostArrays(mmapMemory, expectedCount, () -> {
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
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
            keepLeftmostArrays(metaspaceMemory, expectedCount, () -> new MyClassLoader().findClass("com.tntrip.HelloWorld"));
        }

        @Override
        public String[] prefix() {
            return new String[]{"mt", "Allocate metaspace"};
        }
    }

    public class AllocateHeapCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
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
            int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
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
                T rm = list.remove(i);
                if (rm instanceof Thread) {
                    ((Thread) rm).interrupt();
                }
            }
            System.out.println(String.format("Remove Objects, removedCount=%d, expectedCount=%d, orgnSize=%d", (orgnSize - expectedCount), expectedCount, orgnSize));
            System.out.println();
        }
    }

    public class CalcNumCase implements EachCase {
        private Get24 g = new Get24();

        @Override
        public void doOnLine(String line) {
            String[] strNumbers = line.substring(prefix()[0].length()).split("\\s+");
            int[] nums = Arrays.stream(strNumbers).map(Integer::parseInt).mapToInt(e -> e).toArray();
            Set<String> allFormulas = g.findAllFormulas(nums);

            for (String formula : allFormulas) {
                System.out.println(formula);
            }
            System.out.println("Total: " + allFormulas.size());
            System.out.println();
        }

        @Override
        public String[] prefix() {
            return new String[]{"c", "Get 24"};
        }
    }

    public class ReadMetadataCase implements EachCase {
        @Override
        public void doOnLine(String line) {
            String fullPath = line.substring(prefix()[0].length());
            long begin = System.currentTimeMillis();
            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(new File(fullPath));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    System.out.format("[%s] - %s = %s",
                            directory.getName(), tag.getTagName(), tag.getDescription());
                    System.out.println();
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.format("ERROR: %s", error);
                        System.out.println();
                    }
                }
            }
            System.out.println("Cost: " + (System.currentTimeMillis() - begin) + "ms");
            System.out.println();
        }

        @Override
        public String[] prefix() {
            return new String[]{"m", "Get file metadata"};
        }
    }

    public class TestLockSupportCase implements EachCase {
        private Thread parkThread;
        public TestLockSupportCase() {
            parkThread = new Thread(() -> {
                AtomicInteger count = new AtomicInteger(0);
                while (true) {
                    try {
                        LockSupport.park();
                        System.out.println(DateUtil.date2String(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS) +
                                ": park done, count= " + count.getAndIncrement() +
                                ", interrupted=" + Thread.currentThread().isInterrupted());
                        if (Thread.currentThread().isInterrupted()) {
                            Thread.interrupted();
                        }
                        System.out.println("After recover interrupt status, interrupted:" + Thread.currentThread().isInterrupted());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            parkThread.start();
        }

        @Override
        public void doOnLine(String line) {
            parkThread.interrupt();
        }

        @Override
        public String[] prefix() {
            return new String[]{"u", "park, unpark thread"};
        }
    }

    public static void main(String[] args) {
        AbstractDoCases.run(new KindsOfMemory());
    }
}
