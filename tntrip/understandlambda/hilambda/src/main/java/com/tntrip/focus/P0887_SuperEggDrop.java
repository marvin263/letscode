package com.tntrip.focus;

import org.apache.poi.hssf.dev.ReSave;

import java.util.Arrays;

public class P0887_SuperEggDrop {
    public int superEggDrop(int K, int N) {
        int[][] result = new int[K + 1][N + 1];
        for (int[] row : result) {
            Arrays.fill(row, -1);
        }

        // 只有一个蛋，总是需要N次
        Arrays.fill(result[1], N);
        Arrays.fill(result[0], 0);

        // 只有1层，总是需要1次
        for (int k = 0; k <= K; k++) {
            result[k][1] = 1;
        }

        for (int k = 0; k <= K; k++) {
            result[k][0] = 0;
        }

        return dp(result, K, N);
    }


    public int dp(int[][] result, int K, int N) {
        if(result[K][N]!=-1){
            return result[K][N];
        }

        int finalRst = Integer.MAX_VALUE;
        for (int n = 2; n <= N; n++) {
            finalRst = Math.min(finalRst,
                    1 + Math.max(dp(result, K-1, n-1), dp(result, K, N-n)));
        }
        result[K][N] = finalRst;
        return finalRst;
    }

    public static void main(String[] args) {
        P0887_SuperEggDrop p = new P0887_SuperEggDrop();
        System.out.println(p.superEggDrop(1, 2));//2
        System.out.println(p.superEggDrop(2, 6));//3
        System.out.println(p.superEggDrop(3, 14));//4
    }
}
