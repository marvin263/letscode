package com.tntrip.focus;

public class P1143_LongestCommonSubsequence {
    public int longestCommonSubsequence(String text1, String text2) {
        // 前i字符
        // 前j字符
        // 最长公共子序列 的长度
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        char[] chars1 = text1.toCharArray();
        char[] chars2 = text2.toCharArray();

        for (int i = 1; i <= chars1.length; i++) {
            char c1 = chars1[i - 1];
            for (int j = 1; j <= chars2.length; j++) {
                char c2 = chars2[j - 1];
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = chooseMax(dp[i][j - 1], dp[i - 1][j]);
                }
            }
        }
        return dp[text1.length()][text2.length()];
    }

    private int chooseMax(int... args) {
        int max = 0;
        for (int arg : args) {
            if (max < arg) {
                max = arg;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        P1143_LongestCommonSubsequence p = new P1143_LongestCommonSubsequence();
        System.out.println(p.longestCommonSubsequence("abcxacmnk", "mnkabc"));//3
        System.out.println(p.longestCommonSubsequence("abc", "abc"));//3
        System.out.println(p.longestCommonSubsequence("abc", "def"));//0
    }

}
