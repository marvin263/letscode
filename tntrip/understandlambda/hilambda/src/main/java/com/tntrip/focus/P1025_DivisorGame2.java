package com.tntrip.focus;

public class P1025_DivisorGame2 {
    public boolean divisorGame(int N) {
        boolean[] dp = new boolean[1000 + 1];
        // dp的含义：
        // 给定数字n，先走的人 必定赢 的结果是dp[n]
        // dp[n]==true：先走的人 必定赢
        // dp[n]==false：先走的人 必定输

        // 给定数字n，最终 必定赢，还是必定输？
        // 我们是算法是：基于前面的，递推后面的

        dp[1] = false;
        dp[2] = true;
        for (int n = 3; n <= N; n++) {
            // 余数为0，被除数 只能是 <= n/2 的数
            // 即：被除数 的范围是：[1...middle]
            int middle = n >> 1;
            for (int j = 1; j <= middle; j++) {
                // 被除数为j时，余数为0，且dp[n - j]==false（即 给定数字 n-j，先走的人 必定输）
                if (n % j == 0 && !dp[n - j]) {
                    dp[n] = true;
                    break;
                }
            }
            // 如果上面循环没执行到break，则：dp[n]==false，即：给定数字n，先走的人 必定输
        }
        return dp[N];
    }

    public static void main(String[] args) {
        P1025_DivisorGame2 p = new P1025_DivisorGame2();
        int[] numbers = new int[]{1, 2, 3, 4, 1000};
        for (int n : numbers) {
            System.out.println(n + ": " + p.divisorGame(n));
        }
    }
}
