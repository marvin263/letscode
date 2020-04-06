package com.tntrip.focus;

public class P0152_MaximumProductSubarray {
    public int maxProduct_two_dimension(int[] nums) {
        // dp[i][j]
        // i--j的子数组的乘积（i<=j）
        int[][] dp = new int[nums.length][nums.length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < dp.length; i++) {
            int[] row = dp[i];
            for (int j = i; j < row.length; j++) {
                if (i == j) {
                    dp[i][j] = nums[j];
                } else {
                    dp[i][j] = nums[j] * dp[i][j - 1];
                }
                max = Math.max(max, dp[i][j]);
            }
        }
        return max;
    }

    public int maxProduct_one_dimension(int[] nums) {
        // dp[j]
        // 0-->j 乘积最大值
        int[] dp = new int[nums.length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < dp.length; i++) {
            for (int j = i; j < dp.length; j++) {
                if (i == j) {
                    dp[j] = nums[j];
                } else {
                    dp[j] = nums[j] * dp[j - 1];
                }
                max = Math.max(max, dp[j]);
            }
        }
        return max;
    }

    public int maxProduct(int[] nums) {
        int finalMax;
        int premax, nowmax;
        int premin, nowmin;
        int n = nums.length;
        finalMax = premax = premin = nums[0];
        for (int i = 1; i < n; ++i) {
            int curN = nums[i];
            nowmax = Math.max(curN, Math.max(curN * premax, curN * premin));
            nowmin = Math.min(curN, Math.min(curN * premax, curN * premin));

            finalMax = Math.max(finalMax, nowmax);
            premax = nowmax;
            premin = nowmin;
        }
        return finalMax;
    }

    public static void main(String[] args) {
        P0152_MaximumProductSubarray p = new P0152_MaximumProductSubarray();
        System.out.println(p.maxProduct_two_dimension(new int[]{2, 3, -2, 4}));
        System.out.println(p.maxProduct_two_dimension(new int[]{-2, 0, -1}));
        System.out.println(p.maxProduct_two_dimension(new int[]{1, 2, 3, 4, 1000}));
    }
}
