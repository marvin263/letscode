package com.tntrip.focus;

public class GetTheSum {
    // target: [1, 1000]
    // arr.length: [1, 1000]
    public static void fdfd(int target, int[] arr) {

        int len = Math.max(target, arr.length);

        // dp[i][j]
        // 在 数组前i个数字 中任选组合，使得加和为j，此时，方案的最大个数为 dp[i][j]

        int[][] dp = new int[len + 1][len + 1];
        for (int j = 1; j < dp.length; j++) {
            // 在 数组前1个数字 中任选组合，使得加和为j，此时，方案的最大个数为 dp[1][j]
            dp[1][j] = (arr[0] == j) ? 1 : 0;
        }

        // 在 数组前i个数字 中任选组合
        for (int i = 2; i < dp.length; i++) {
            int val = arr[i];

            //使得加和为j
            for (int j = 1; j < dp.length; j++) {
                // 不使用i时，就可以加和为j 的方案个数
                dp[i][j] = dp[i - 1][j];

                if (j > arr[i]) {
                    // 不使用i时，可以加和为 j-arr[i] 的方案个数
                    int tmp = dp[i - 1][j - arr[i]];
                    dp[i][j] = dp[i][j] + tmp;
                }

            }


        }

    }

}
