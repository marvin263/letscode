package com.tntrip;

public class P494_TargetSum {

    public enum Symbol {
        ADD, SUB
    }

    public static class EachStep {
        /**
         * 加上当前的action，总共的action数量
         */
        final int curSum;
        final Symbol action;

        private EachStep(final int curSum, final Symbol action) {
            this.curSum = curSum;
            this.action = action;
        }

        public static EachStep create(int curSum, Symbol action) {
            return new EachStep(curSum, action);
        }
    }

    public int findTargetSumWays(int[] nums, int S) {
        int sumCount = 2001;
        // 前i个数，最终加和为j
        // 此时 符号组合 的次数 dp[i][j] 
        int[][] dp = new int[nums.length + 1][sumCount];

        dp[1][toIdx(S)] = dp[1][toIdx(S)] + 1;
        dp[1][toIdx(-S)] = dp[1][toIdx(-S)] + 1;

        for (int i = 1; i < nums.length; i++) {
            for (int s = -1000; s <= 1000; s++) {
                int appearCount = dp[i][toIdx(s)];
                // 出现次数有，s确实出现了
                if (appearCount > 0) {
                    int newS = s + nums[i];
                    dp[i + 1][toIdx(newS)] = dp[i + 1][toIdx(newS)] + appearCount;

                    int newS2 = s - nums[i];
                    dp[i + 1][toIdx(newS2)] = dp[i + 1][toIdx(newS)] + appearCount;

                }
            }
        }
        return dp[nums.length][toIdx(S)];
    }

    private int toIdx(int aSumValue) {
        return aSumValue >= 0 ? aSumValue : (1000 - aSumValue);
    }



    public static void main(String[] args) {
        P494_TargetSum p = new P494_TargetSum();
        int[] nums = new int[]{1, 1, 1, 1, 1};
        int S = 3;
        System.out.println(p.findTargetSumWays(nums, S));
    }
}
