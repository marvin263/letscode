package com.tntrip.focus;

public class P0279_PerfectSquares {
    public int numSquares(int n) {
        // 给定的数字i，最少需要dp[i]个数（同一个数出现一次就加一次1）
        // 每一个数平方然后加和，等于i
        int[] dp = new int[n + 1];
        for (int eachN = 1; eachN <= n; eachN++) {
            int r = (int) Math.sqrt(eachN);
            int leastCount = Integer.MAX_VALUE;
            for (int j = 1; j <= r; j++) {
                leastCount = Math.min(leastCount, (dp[eachN - j * j] + 1));
            }
            dp[eachN] = leastCount;
        }
        return dp[n];
    }

    public static void main(String[] args) {
        P0279_PerfectSquares p = new P0279_PerfectSquares();
        System.out.println(p.numSquares(12));//3
        System.out.println(p.numSquares(13));//2
    }
}
