package com.tntrip;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticTest {
    public List<String> arithmeticTest(int start, int end) {
        if (start == end) {
            throw new RuntimeException(String.format("Require: start != end. Actually: start=%d, end=%d", start, end));
        }

        int[] nums = new int[Math.abs(start - end) + 1];
        boolean incr = (end - start) > 0;
        for (int i = 0; i < nums.length; i++) {
            nums[i] = start + (incr ? i : -i);
        }

        List<String> result = new ArrayList<>();
        List<List<Integer>> permutation = operatorPermutation(nums.length - 1);
        for (List<Integer> operators : permutation) {
            StringBuilder sb = new StringBuilder();
            sb.append(start);
            int rst = start;
            for (int i = 1; i < nums.length; i++) {
                Integer opr = operators.get(i - 1);
                if (opr == 1) {
                    rst = rst + nums[i];
                    sb.append(" + " + nums[i]);
                } else {
                    rst = rst - nums[i];
                    sb.append(" - " + nums[i]);
                }
            }
            sb.append(" = " + rst);
            result.add(sb.toString());
            //System.out.println(sb.toString());
        }
        return result;
    }

    private List<List<Integer>> operatorPermutation(int numberCount) {
        if (!(numberCount >= 1)) {
            throw new RuntimeException(String.format("Require: numberCount >= 1. Actually: numberCount=%d", numberCount));
        }

        List<List<Integer>> result = new ArrayList<>();
        // 1个数字时，则是最后一位全部的排列方式，也就是2^1种方式
        // 3个数字时，则是最后三位全部的排列方式，也就是2^3种方式
        int permutationCount = 1 << numberCount;
        for (int n = 0; n <= (permutationCount - 1); n++) {
            List<Integer> row = new ArrayList<>();
            result.add(row);
            // n就是一种组合方式。n的最后面这几个bit，其0,1组合方式
            for (int bitIndex = 0; bitIndex < numberCount; bitIndex++) {
                row.add(extractBit(n, bitIndex));
            }
        }
        //System.out.println(String.format("numberCount=%d, permutationCount=%d", numberCount, permutationCount));
        return result;
    }

    /**
     * 给定n，给定bit是0还是1
     *
     * @param n
     * @param whichBit, 0-based，最低位是0，最高位是符号位
     * @return
     */
    public static int extractBit(int n, int whichBit) {
        int mask = Integer.MIN_VALUE >>> (31 - whichBit);
        if ((mask & n) == mask) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(extractBit(4, 0));
        ArithmeticTest at = new ArithmeticTest();
        at.arithmeticTest(1, 3);
        at.arithmeticTest(1, 6);

        at.arithmeticTest(9, 3);

    }
}
