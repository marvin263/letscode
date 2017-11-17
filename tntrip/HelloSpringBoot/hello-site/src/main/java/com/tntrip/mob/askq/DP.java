package com.tntrip.mob.askq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nuc on 2017/11/4.
 */
public class DP {
    private static final Logger LOG = LoggerFactory.getLogger(DP.class);

    public static int findMaxForm(String[] strs, int m, int n) {
        int[][] dp = new int[m + 1][n + 1];
        for (String str : strs) {
            int[] count = count(str);
            for (int i = m; i >= count[0]; i--) {
                System.out.println();
                LOG.info("str={}, count_0={}, count_1={}", str, count[0], count[1]);
                for (int j = n; j >= count[1]; j--) {
                    int before = dp[i][j];
                    dp[i][j] = Math.max(dp[i][j], dp[i - count[0]][j - count[1]] + 1);
                    LOG.info("i={}, j={}, 0数量={}, 1数量={}. 计算前的d[{}][{}]={}, dp[{}-{}][{}-{}]==dp[{}][{}]=={}, 最终的d[{}][{}]={}",
                            i, j, count[0], count[1], i, j, before,
                            i, count[0], j, count[1], (i - count[0]), (j - count[1]), dp[i - count[0]][j - count[1]], i, j, dp[i][j]);
                }
            }
        }
        return dp[m][n];
    }

    private static int[] count(String s) {
        int[] result = new int[2];
        char[] array = s.toCharArray();
        for (int i : array) {
            result[i - '0']++;
        }
        return result;
    }

    public static void main(String[] args) {
        String[] strs = {"10", "0001", "111001", "1", "0"};
        int ddd = findMaxForm(strs, 5, 3);
        System.out.println(ddd);
    }
}
