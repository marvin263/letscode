package com.tntrip.docases;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllocateMmapCase implements EachCase {
    public static final String MM_FILE_NAME = "bigfile.data";
    private List<MappedByteBuffer> mmapMemory = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        String mmFile = Thread.currentThread().getContextClassLoader().getResource("").getPath() + MM_FILE_NAME;
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(mmapMemory, expectedCount, () -> {
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
