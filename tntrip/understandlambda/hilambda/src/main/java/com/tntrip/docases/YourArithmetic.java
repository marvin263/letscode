package com.tntrip.docases;

import com.tntrip.ArithmeticTest;

import java.util.List;

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
