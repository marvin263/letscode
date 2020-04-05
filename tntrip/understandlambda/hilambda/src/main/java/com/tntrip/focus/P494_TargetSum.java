package com.tntrip.focus;

public class P494_TargetSum {
    public int findTargetSumWays(int[] nums, int S) {
        // 前i个数，最终加和为j
        // 此时 符号组合 的次数 dp[i][j] 
        int[][] dp = new int[nums.length + 1][2001];

        // 前1个数，和 nums[0] 时，符号组合 的次数
        dp[1][toIdx(nums[0])] = dp[1][nums[0]] + 1;
        // 前1个数，和 -nums[0] 时，符号组合 的次数
        dp[1][toIdx(-nums[0])] = dp[1][toIdx(-nums[0])] + 1;

        for (int i = 1; i < nums.length; i++) {
            // 前i个数，和分别为各个s 时，符号组合 的次数
            for (int s = -1000; s <= 1000; s++) {
                // 前i个数，和s 时，符号组合 的次数
                int symboCombineCount = dp[i][toIdx(s)];
                // 出现次数有，s确实出现了
                if (symboCombineCount > 0) {
                    int newSIdx1 = toIdx(s + nums[i]);
                    dp[i + 1][newSIdx1] = dp[i + 1][newSIdx1] + symboCombineCount;

                    int newSIdx2 = toIdx(s - nums[i]);
                    dp[i + 1][newSIdx2] = dp[i + 1][newSIdx2] + symboCombineCount;

                }
            }
        }
        return Math.abs(S) > 1000 ? 0 : dp[nums.length][toIdx(S)];
    }

    private int toIdx(int aSumValue) {
        return aSumValue >= 0 ? aSumValue : (1000 - aSumValue);
    }


    public static void main(String[] args) {
        P494_TargetSum p = new P494_TargetSum();
        int[] nums = new int[]{1, 2, 7, 9, 981};
        int S = 1000000000;
        System.out.println(p.findTargetSumWays(nums, S));
    }
}
