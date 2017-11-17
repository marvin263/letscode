package com.tntrip.understand.algorithm;

/**
 * Created by nuc on 2016/9/15.
 */
public class Knapsack {
    public static int[][] maxValue(int[] weights, int values[], int capacity) {
        int[][] d = new int[weights.length][weights.length];

        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < capacity; j++) {
                int maxValueWhenUseI = i == 0 ? 0 : d[i - 1][j];// i号 并不放入

                int maxValueWhenNotUseI = 0;
                if (i > 0 && j >= values[i]) {
                    maxValueWhenNotUseI = d[i - 1][j - weights[i]] + weights[i];// i号 放入
                }
                d[i][j] = Math.max(maxValueWhenUseI, maxValueWhenNotUseI);
            }
        }
        return d;
    }

    public static int[] selection(int[][] d) {
        int[] selection = new int[d.length];
        for (int i = d.length - 1; i >= 0; i--) {

        }


        return selection;
    }

    public static void main(String[] args) {
        int[][] aa = new int[3][1];
        aa[0] = new int[]{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9};
        aa[1] = new int[]{1, 1, 1, 1};
        aa[2] = new int[]{2};

        System.out.println(aa.length);
        for (int i = 0; i < aa.length; i++) {
            int[] eachRow = aa[i];
            System.out.println(eachRow.length);
        }
        System.out.println(aa[0][9]);


    }
}
