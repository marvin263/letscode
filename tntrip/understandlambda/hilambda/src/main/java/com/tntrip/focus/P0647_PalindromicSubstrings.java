package com.tntrip.focus;

public class P0647_PalindromicSubstrings {
    public int countSubstrings(String s) {
        if (s.length() == 0) {
            return 0;
        }

        // 子串s(i--j)时，该子串 是否是 回文子串
        boolean[][] dp = new boolean[s.length()][s.length()];
        int count = 0;
        char[] chars = s.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            char c1 = chars[i];
            for (int j = i; j <= chars.length - 1; j++) {
                if (i == j) {
                    dp[i][j] = true;
                } else {
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
                
                if (dp[i][j]) {
                    count++;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        P0647_PalindromicSubstrings p = new P0647_PalindromicSubstrings();
        System.out.println(p.countSubstrings("aaa"));//aa
        System.out.println(p.countSubstrings("abc"));//bab, aba
    }

}
