package com.tntrip.focus;


import java.util.Arrays;

public class P0354_RussianDollEnvelopes {
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (arr1, arr2) -> {
            if (arr1[0] != arr2[0]) {
                return arr1[0] - arr2[0];
            } else {
                return arr1[1] - arr2[1];
            }
        });
        return lengthOfLIS(envelopes);
    }

    public int lengthOfLIS(int[][] nums) {
        // 以nums[i]结尾的 最长递增子序列的长度
        int[] dp = new int[nums.length];
        int maxLIS = 0;
        for (int i = 0; i < nums.length; i++) {
            int LIS = 1;
            // 前面的dp[j]都已经得到了，利用前面的这些值，求得dp[i]
            // 永远都是 暴力枚举。枚举出来：
            // dp[i]必须要以nums[i]结尾
            for (int j = 0; j < i; j++) {
                // 该值是以 nums[i]结尾的
                if (nums[i][0] > nums[j][0] && nums[i][1] > nums[j][1]) {
                    LIS = Math.max(LIS, dp[j] + 1);
                }
                // 从该dp[j]无法得到 以nums[i]结尾的dp[i]
                else {

                }
            }
            dp[i] = LIS;
            maxLIS = Math.max(maxLIS, LIS);
        }
        return maxLIS;
    }

    public static void main(String[] args) {
        P0354_RussianDollEnvelopes p = new P0354_RussianDollEnvelopes();

        System.out.println(p.maxEnvelopes(new int[][]{{4, 5}, {4, 6}, {6, 7}, {2, 3}, {1, 1}}));//4
        System.out.println(p.maxEnvelopes(new int[][]{{1, 3}, {3, 5}, {6, 7}, {6, 8}, {8, 4}, {9, 5}}));//3
        System.out.println(p.maxEnvelopes(new int[][]{{5, 4}, {6, 4}, {6, 7}, {2, 3}}));//3


    }
}
