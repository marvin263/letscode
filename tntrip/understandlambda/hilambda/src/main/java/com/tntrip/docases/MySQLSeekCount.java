package com.tntrip.docases;

import com.tntrip.understand.EstimatingQueryPerformance;

import java.util.Arrays;

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
