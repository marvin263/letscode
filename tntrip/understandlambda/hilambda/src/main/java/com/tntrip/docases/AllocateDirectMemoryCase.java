package com.tntrip.docases;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AllocateDirectMemoryCase implements EachCase {
    private List<ByteBuffer> directMemory = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(directMemory, expectedCount, () -> ByteBuffer.allocateDirect(HappyDoCases.M));
    }

    @Override
    public String[] prefix() {
        return new String[]{"dm", "Allocate direct memory"};
    }
}
