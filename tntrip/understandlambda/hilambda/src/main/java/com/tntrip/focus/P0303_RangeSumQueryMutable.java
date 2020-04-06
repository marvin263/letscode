package com.tntrip.focus;

public class P0303_RangeSumQueryMutable {
    private int[] dp;

    public P0303_RangeSumQueryMutable(int[] nums) {
        dp = new int[nums.length];
        if (nums.length <= 0) {
            return;
        }
        dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = nums[i] + dp[i - 1];
        }
    }

    public int sumRange(int i, int j) {
        if (i == 0) {
            return dp[j];
        }
        return dp[j] - dp[i - 1];
    }

    public static void main(String[] args) {
        int[] numbers = new int[]{-2, 0, 3, -5, 2, -1};
        P0303_RangeSumQueryMutable p = new P0303_RangeSumQueryMutable(numbers);
        System.out.println(p.sumRange(0, 2));
        System.out.println(p.sumRange(2, 5));
        System.out.println(p.sumRange(0, 5));
    }
}
