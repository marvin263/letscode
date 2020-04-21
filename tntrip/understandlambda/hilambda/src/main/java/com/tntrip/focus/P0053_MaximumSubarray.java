package com.tntrip.focus;

public class P0053_MaximumSubarray {
    public int maxSubArray(int[] nums) {
        // 前i个元素，最大和为 dp[i]
        int[] dp = new int[nums.length + 1];
        for (int i = 1; i <= nums.length; i++) {
            int v = nums[i - 1];
            for (int j = 0; j <= i; j++) {
                //dp[j] = Math.max((dp[i]+v))
            }
        }
        return dp[nums.length];

    }

    public static void main(String[] args) {
        P0053_MaximumSubarray p = new P0053_MaximumSubarray();

        System.out.println(p.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));//6
    }
}
