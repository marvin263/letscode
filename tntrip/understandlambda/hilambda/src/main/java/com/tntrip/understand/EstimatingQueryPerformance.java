package com.tntrip.understand;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EstimatingQueryPerformance {

    public static double log(double d, double based) {
        return Math.log10(d) / Math.log10(based);
    }

    public static double estimate(int row_count, int index_length) {
        double cost = doEstimate(row_count, 1024, index_length, 4);
        BigDecimal bd = new BigDecimal(cost);
        return bd.setScale(3, RoundingMode.CEILING).doubleValue();
    }

    // log(row_count) / 
    // log(index_block_length / 3 * 2 / (index_length + data_pointer_length))
    // +1
    public static double doEstimate(int row_count, int index_block_length, int index_length, int data_pointer_length) {
        int entryLength = index_length + data_pointer_length;
        double entryCountPerIndexBlock = index_block_length * 2D / 3D / entryLength;
        return (log(row_count, entryCountPerIndexBlock) + 1);
    }

    public static void main(String[] args) {
        System.out.println(estimate(500000, 3));
    }

}
