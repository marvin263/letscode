package com.tntrip.focus;

public class P516_LongestPalindromicSubsequence {
    public int longestPalindromeSubseq(String s) {
        if (s.length() == 0) {
            return 0;
        }
        // 子串 s(i--j)时
        // 最长回文子序列 的长度
        int[][] dp = new int[s.length()][s.length()];
        // i和j相等时，只有一个字符，设置为1
        for (int i = 0; i < dp.length; i++) {
            int[] ints = dp[i];
            for (int j = 0; j < ints.length; j++) {
                if (i == j) {
                    dp[i][j] = 1;
                }
            }
        }
        char[] chars = s.toCharArray();
        for (int i = chars.length - 2; i >= 0; i--) {
            char c1 = chars[i];
            for (int j = i + 1; j <= chars.length - 1; j++) {
                char c2 = chars[j];
                if (c1 == c2) {
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[0][s.length() - 1];
    }

    public static void main(String[] args) {
        P516_LongestPalindromicSubsequence p = new P516_LongestPalindromicSubsequence();
        System.out.println(p.longestPalindromeSubseq("aacdefcaa"));//7
        System.out.println(p.longestPalindromeSubseq("bbbab"));//4
    }

}
