package com.tntrip.focus;

public class P0115_DistinctSubsequences {
    public int numDistinct(String s, String t) {
        // 正好等于t的 s的子序列的个数
        // dp[i][j]
        // t 取前i个字符的字符串  s取前j个字符的字符串 然后所构成的 子序列
        // 个数为 dp[i][j]

        int[][] dp = new int[t.length() + 1][s.length() + 1];
        for (int j = 1; j <= s.length(); j++) {
            if (t.charAt(0) == s.charAt(j - 1)) {
                dp[1][j] = dp[1][j - 1] + 1;
            } else {
                dp[1][j] = dp[1][j - 1];
            }
        }

        char[] tArr = t.toCharArray();
        char[] sArr = s.toCharArray();
        for (int i = 2; i <= tArr.length; i++) {
            char tc = tArr[i - 1];
            for (int j = 1; j <= sArr.length; j++) {
                char sc = sArr[j - 1];
                if (tc == sc) {
                    dp[i][j] = dp[i - 1][j - 1] + dp[i][j - 1];
                } else {
                    dp[i][j] = dp[i][j - 1];
                }
            }
        }

        return dp[t.length()][s.length()];
    }

    public static void main(String[] args) {
        P0115_DistinctSubsequences p = new P0115_DistinctSubsequences();
        System.out.println(p.numDistinct("rabbbit", "rabbit"));//3
        System.out.println(p.numDistinct("babgbag", "bag"));//5
    }

}
