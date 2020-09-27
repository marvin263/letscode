package com.tntrip.docases;

import com.tntrip.MyClassLoader;

import java.util.ArrayList;
import java.util.List;

public class AllocateMetaspaceCase implements EachCase {
    private List<Class<?>> metaspaceMemory = new ArrayList<>();

    @Override
    public void doOnLine(String line) {
        int expectedCount = Integer.parseInt(line.substring(prefix()[0].length()));
        HappyDoCases.keepLeftmostArrays(metaspaceMemory, expectedCount, () -> new MyClassLoader().findClass("com.tntrip.HelloWorld"));
    }

    @Override
    public String[] prefix() {
        return new String[]{"mt", "Allocate metaspace"};
    }
}
