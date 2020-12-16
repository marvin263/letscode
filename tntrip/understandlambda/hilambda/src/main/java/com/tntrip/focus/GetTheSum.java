package com.tntrip.focus;

import java.util.Arrays;
import java.util.Scanner;

public class GetTheSum {
    public static void main(String[] args) {
        //testIt();
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        String[] lines = new String[2];
        while (i <= 1 && scanner.hasNextLine()) {
            lines[i] = scanner.nextLine();
            i++;
        }

        String[] nAndSum = lines[0].split("\\s+");
        int n = Integer.parseInt(nAndSum[0]);
        int sum = Integer.parseInt(nAndSum[1]);
        int[] arr = Arrays.stream(lines[1].split("\\s+")).mapToInt(Integer::parseInt).toArray();

        long count = solve(sum, arr);
        //System.out.println("sum=" + sum + ", arr=" + Arrays.toString(arr) + ", count=" + count);
        System.out.println(count);
    }

    // target: [0, 1000]
    // values.length: [1, 1000]
    public static long solve(int target, int[] values) {
        int iCount = values.length + 1;
        int jCount = target + 1;
        // dp[i][j]
        // 在 数组前i个数字 中任选组合，使得加和为j时，方案的最大个数为 dp[i][j]
        long[][] dp = new long[iCount][jCount];

        for (int j = 0; j < jCount; j++) {
            // 在 数组前1个数字 中任选组合，使得加和为j时，方案的最大个数为 dp[1][j]
            dp[1][j] = (values[0] == j) ? 1 : 0;
        }

        // 在 数组前i个数字 中任选组合
        for (int i = 2; i < iCount; i++) {
            int val = values[i - 1];
            //使得加和为j
            for (int j = 1; j < jCount; j++) {
                // 不使用i时，就可以加和为j 的方案个数
                dp[i][j] = dp[i - 1][j];

                if (j == val) {
                    // 单单 values[i - 1] 就正好形成了一种方案
                    dp[i][j] = dp[i][j] + 1;
                    // 不使用i时，可以加和为 0 的方案个数
                    long tmp = dp[i - 1][0];
                    dp[i][j] = dp[i][j] + tmp;
                } else if (j > val) {
                    // 不使用i时，可以加和为 j-values[i-1] 的方案个数
                    long tmp = dp[i - 1][j - val];
                    dp[i][j] = dp[i][j] + tmp;
                }
                // values[i - 1]本身就已经 > j  了，任何方案都无法使用 values[i - 1] 啦
                else {

                }
            }

        }
        return dp[values.length][target];
    }

    public static void testIt() {
        int sum = 15;
        int[] arr = new int[]{5, 5, 10, 2, 3};
        long count = solve(sum, arr);
        System.out.println("sum=" + sum + ", arr=" + Arrays.toString(arr) + ", count=" + count);
    }
}
