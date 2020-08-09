package com.tntrip;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.tntrip.get24.Get24;
import com.tntrip.tidyfile.DateUtil;
import com.tntrip.understand.EstimatingQueryPerformance;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;
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
 *  -Xms1024m -Xmx1024m -XX:+HeapDumpBeforeFullGC -XX:+HeapDumpAfterFullGC -XX:HeapDumpPath=E:/dump/test.hdump
 *
 *  -XX:+PrintFlagsFinal -Xms1024m -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -Xloggc:E:/dump/mygc.log
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

    public static class LetsSocket {
        private OutputStreamWriter osw;
        private volatile boolean broken = false;

        public LetsSocket(String host, int port) {
            try {
                Socket s = new Socket(host, port);
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                new Thread(() -> {
                    try {
                        readSocket(isr);
                        markAsBroken();
                    } catch (Exception e) {
                        e.printStackTrace();
                        markAsBroken();
                    }
                }).start();
                osw = new OutputStreamWriter(s.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        private void markAsBroken() {
            broken = true;
        }

        private boolean isBroken() {
            return broken;
        }

        private void readSocket(InputStreamReader isr) throws Exception {
            char[] buf = new char[256];
            int round = 0;
            while (true) {
                if (broken) {
                    break;
                }
                int count = isr.read(buf);
                if (count == -1) {
                    System.out.println("round=" + (++round) + ", count=" + count + "--------the end of the stream has been reached. Done");
                    break;
                }
                System.out.println("round=" + (++round) + ", count=" + count + "--------" + new String(buf, 0, count));
            }
            System.out.println("read done");
        }

        public void writeSocket(String str) {
            if (broken) {
                return;
            }
            try {
                if (str == null || str.length() == 0) {
                    osw.write("\r\n");
                } else if (str.startsWith("1")) {
                    osw.write(str.substring(1));
                } else {
                    osw.write(str + "\r\n");
                }
                osw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                markAsBroken();
            }
        }
    }

    public class ReadWriteSocket implements EachCase {
        private LetsSocket ls =null;
                //new LetsSocket("192.168.0.7", 8080);

        @Override
        public void doOnLine(String line) {
            String actualLine = line.substring(prefix()[0].length());
            if (ls.isBroken()) {
                ls = new LetsSocket("192.168.0.7", 8080);
                System.out.println("Create socket done. ls=" + ls);
            }
            ls.writeSocket(actualLine);
        }

        @Override
        public String[] prefix() {
            return new String[]{"w", "Write socket"};
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
            return new String[]{"mf", "Get file metadata"};
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

    public class RemainderIsZero implements EachCase {
        @Override
        public void doOnLine(String line) {
            int n = Integer.parseInt(line.substring(prefix()[0].length()));
            int middle = n / 2;
            int count = 0;
            for (int i = 1; i <= middle; i++) {
                if (n % i == 0) {
                    System.out.println(i);
                    count++;
                }
            }
            System.out.println(n + "--> finds " + count + " factors");
        }

        @Override
        public String[] prefix() {
            return new String[]{"f", "Factors whose remainder is 0"};
        }
    }

    public class YourArithmetic implements EachCase {
        private ArithmeticTest at = new ArithmeticTest();

        @Override
        public void doOnLine(String line) {
            String[] startEnd = line.substring(prefix()[0].length()).split(",");
            int start = Integer.parseInt(startEnd[0]);
            int end = Integer.parseInt(startEnd[1]);
            List<String> strings = at.arithmeticTest(start, end);
            strings.forEach(System.out::println);
            System.out.println("\n");
            strings.stream().map(e -> {
                int pos = e.indexOf("=");
                String prefix = e.substring(0, pos);
                String prefix2 = prefix.replaceAll("\\Q+\\E", " ").replaceAll("\\Q-\\E", " ");
                return prefix2 + e.substring(pos);
            }).forEach(System.out::println);

            System.out.println(String.format("start=%d, end=%d, finally find %d equations", start, end, strings.size()));
        }

        @Override
        public String[] prefix() {
            return new String[]{"a", "arithmetic test"};
        }
    }

    public class MySQLSeekCount implements EachCase {
        @Override
        public void doOnLine(String line) {
            String[] strNumbers = line.substring(prefix()[0].length()).split("\\s+");
            int[] nums = Arrays.stream(strNumbers).map(Integer::parseInt).mapToInt(e -> e).toArray();
            int actualRowCount = nums[0] * 10000;
            System.out.println("row_count= " + actualRowCount + ", index_length=" + nums[1] + ", need seek_count=" + EstimatingQueryPerformance.estimate(actualRowCount, nums[1]));
            System.out.println();
        }

        @Override
        public String[] prefix() {
            return new String[]{"s", "seek, Given row_count(unit万) and index_length, estimate seek_count"};
        }
    }

    public static void main(String[] args) {
        AbstractDoCases.run(new KindsOfMemory());
    }
}
