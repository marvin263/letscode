package com.tntrip.focus;

import java.util.Arrays;

public class P0887_SuperEggDrop {
    /**
     * firstly, if we have k eggs and s steps to detect a building with Q(k, s) floors,
     *
     * secondly, we use 1 egg and 1 step to detect one floor,
     * if egg break, we can use (k-1) eggs and (s-1) to detect with Q(k-1, s-1),
     * 
     * if egg isn't broken, we can use k eggs and (s-1) step to detech with Q(k, s-1),
     * So, Q(k, s) = 1 + Q(k, s-1) + Q(k-1, s-1);
     *
     * dp[i] is max floors we can use i eggs and s step to detect.
     * 
     * @param K
     * @param N
     * @return
     */
    public int superEggDropd(int K, int N) {
        int[] dp = new int[K + 1];
        int step = 0;
        for (; dp[K] < N; step++) {
            for (int i = K; i > 0; i--)
                dp[i] = (1 + dp[i] + dp[i - 1]);
        }
        return step;
    }

    public int superEggDrop(int K, int N) {
        int[][] result = new int[K + 1][N + 1];
        for (int[] row : result) {
            Arrays.fill(row, -1);
        }

        // 只有1个蛋，n层则需要n次
        for (int n = 0; n <= N; n++) {
            result[1][n] = n;
        }
        // 只有1层，无论几个蛋，总是需要1次
        for (int k = 0; k <= K; k++) {
            result[k][1] = 1;
        }

        // 只有0个蛋，总是需要0次
        Arrays.fill(result[0], 0);
        // 只有0层，总是需要0次
        for (int k = 0; k <= K; k++) {
            result[k][0] = 0;
        }

        return dp(result, K, N);
    }


    public int dp(int[][] result, int K, int N) {
        if (result[K][N] != -1) {
            return result[K][N];
        }

        int finalRst = Integer.MAX_VALUE;
        for (int n = 1; n <= N; n++) {
            finalRst = Math.min(finalRst,
                    Math.max(dp(result, K - 1, n - 1), dp(result, K, N - n))+1
            );
        }
        result[K][N] = finalRst;
        //System.out.println("K=" + K + ", N=" + N + ", finalRst=" + finalRst);
        return finalRst;
    }

    public static void main(String[] args) {
        P0887_SuperEggDrop p = new P0887_SuperEggDrop();
        System.out.println(p.superEggDrop(2, 10));//2
//        System.out.println(p.superEggDrop(2, 6));//3
//        System.out.println(p.superEggDrop(3, 14));//4
//        System.out.println(p.superEggDrop(2, 100));//14
//        System.out.println(p.superEggDrop(5, 10000));//14
    }
}
