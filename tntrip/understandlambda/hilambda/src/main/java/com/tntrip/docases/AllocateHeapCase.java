package com.tntrip.docases;

import java.util.ArrayList;
import java.util.List;

public class AllocateHeapCase implements EachCase {
    private List<byte[]> heapMemory = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(heapMemory, expectedCount, () -> new byte[HappyDoCases.M]);
    }

    @Override
    public String[] prefix() {
        return new String[]{"h", "Allocate heap space"};
    }
}
