package com.tntrip.focus;

public class P0053_MaximumSubarray {
    public int maxSubArray(int[] nums) {
        // 以元素i作为末尾元素时，i以及前面元素 所形成的 连续数字的最大和 dp[i]
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int v = nums[i];
            if (v > 0) {
                dp[i] = dp[i - 1] + v;
            } else {
                dp[i] = dp[i - 1];
            }
        }
        return dp[nums.length - 1];
    }

    public static void main(String[] args) {
        P0053_MaximumSubarray p = new P0053_MaximumSubarray();
        System.out.println(p.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));//6
    }
}
