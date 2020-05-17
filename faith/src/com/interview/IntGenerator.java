package com.interview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class IntGenerator implements Iterator<Integer> {
    private int count;
    private int consumed;
    private List<Integer> all = new ArrayList<>();

    public IntGenerator(int count) {
        this.count = count;
    }

    @Override
    public boolean hasNext() {
        return consumed < count;
    }

    @Override
    public Integer next() {
        consumed++;
        int i = new Random().nextInt(1000);
        all.add(i);
        return i;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(all.toString());
        sb.append("\n");
        Collections.sort(all, Comparator.reverseOrder());
        sb.append(all.toString());
        return sb.toString();
    }
}
