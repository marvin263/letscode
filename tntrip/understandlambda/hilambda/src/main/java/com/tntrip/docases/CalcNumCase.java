package com.tntrip.docases;

import com.tntrip.get24.Get24;

import java.util.Arrays;
import java.util.Set;

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
