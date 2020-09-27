package com.tntrip.docases;

public class GcCase implements EachCase {
    @Override
    public void doOnLine(String line) {
        System.gc();
        System.out.println("gc done");
        System.out.println();
    }

    @Override
    public String[] prefix() {
        return new String[]{"gc", "gc"};
    }
}
