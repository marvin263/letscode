package com.tntrip.focus;

public class P5_LongestPalindromicSubstring {
    public String longestPalindrome(String s) {
        if (s.length() == 0) {
            return "";
        }

        // 子串s(i--j)时，该子串 是否是 回文子串
        boolean[][] dp = new boolean[s.length()][s.length()];
        // i和j相等时，只有一个字符，设置为true
        for (int i = 0; i < dp.length; i++) {
            boolean[] row = dp[i];
            for (int j = 0; j < row.length; j++) {
                if (i == j) {
                    dp[i][j] = true;
                }
            }
        }

        char[] chars = s.toCharArray();
        for (int i = chars.length - 2; i >= 0; i--) {
            char c1 = chars[i];
            for (int j = i + 1; j <= chars.length - 1; j++) {
                char c2 = chars[j];
                if (c1 == c2) {
                    // 只有两个字符时
                    if (j - i <= 1) {
                        dp[i][j] = true;
                    } else {
                        dp[i][j] = dp[i + 1][j - 1];
                    }
                } else {
                    dp[i][j] = false;
                }
            }
        }

        int maxLength = -1;
        int theI = 0;
        int theJ = 0;
        for (int i = 0; i < dp.length; i++) {
            boolean[] row = dp[i];
            for (int j = i; j <= row.length - 1; j++) {
                if (dp[i][j] && (j - i) > maxLength) {
                    theI = i;
                    theJ = j;
                    maxLength = j - i;
                }
            }
        }
        return s.substring(theI, theJ + 1);
    }

    public static void main(String[] args) {
        P5_LongestPalindromicSubstring p = new P5_LongestPalindromicSubstring();
        System.out.println(p.longestPalindrome("aacdefcaa"));//aa
        System.out.println(p.longestPalindrome("babad"));//bab, aba
    }

}
