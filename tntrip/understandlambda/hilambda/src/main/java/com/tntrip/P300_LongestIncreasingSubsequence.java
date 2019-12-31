package com.tntrip;


public class P300_LongestIncreasingSubsequence {
    public int lengthOfLIS(int[] nums) {
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
                if (nums[i] > nums[j]) {
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
        P300_LongestIncreasingSubsequence p = new P300_LongestIncreasingSubsequence();

        System.out.println(p.lengthOfLIS(new int[]{4, 10, 4, 3, 8, 9}));//3
        System.out.println(p.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));//4
        System.out.println(p.lengthOfLIS(new int[]{1, 2, 3, 5}));//false
    }
}
