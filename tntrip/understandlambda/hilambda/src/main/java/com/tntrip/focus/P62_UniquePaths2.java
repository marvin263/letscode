package com.tntrip.focus;

public class P62_UniquePaths2 {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        // 到达 位置(i,j) 总共的路径数量
        int[][] dp = new int[obstacleGrid.length][obstacleGrid[0].length];
        for (int i = 0; i < dp.length; i++) {
            int[] row = dp[i];
            for (int j = 0; j < row.length; j++) {
                // 障碍物，都设置为0。
                // 即：到达该位置，有0种方式
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0;
                }
                // 不是障碍物，但，(0, 0)，则设置为1
                else if (i == 0 && j == 0) {
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
        P62_UniquePaths2 p = new P62_UniquePaths2();
        System.out.println(p.uniquePathsWithObstacles(new int[][]{{0, 1}}));//2
        System.out.println(p.uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}));//2
    }

}
