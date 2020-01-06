package com.tntrip;

public class P62_UniquePaths {
    public int uniquePaths(int m, int n) {
        // 到达 位置(i,j) 总共的路径数量
        int[][] dp = new int[m][n];
        for (int i = 0; i < dp.length; i++) {
            int[] row = dp[i];
            for (int j = 0; j < row.length; j++) {
                if (i == 0 && j == 0) {
                    dp[i][j] = 1;
                } else {
                    dp[i][j] = cellValue(dp, i - 1, j) + cellValue(dp, i, j - 1);
                }
            }
        }
        return dp[dp.length - 1][dp[0].length - 1];
    }

    private int cellValue(int[][] dp, int curI, int curJ) {
        if (curI < 0 || curJ < 0) {
            return 0;
        }
        return dp[curI][curJ];
    }


    public static void main(String[] args) {
        P62_UniquePaths p = new P62_UniquePaths();
        System.out.println(p.uniquePaths(3, 2));//3
        System.out.println(p.uniquePaths(7, 3));//28
    }

}
