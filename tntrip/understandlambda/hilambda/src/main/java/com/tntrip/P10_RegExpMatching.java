package com.tntrip;

public class P10_RegExpMatching {
    /**
     * '.' 匹配任意单个字符
     * '*' 匹配零个或多个前面的那一个元素
     * <p>
     * s 可能为空，且只包含从 a-z 的小写字母。
     * p 可能为空，且只包含从 a-z 的小写字母，以及字符 . 和 *。
     *
     * @param s
     * @param p
     * @return
     */
    public boolean isMatch(String s, String p) {
        if (s.length() == 0 && p.length() == 0) {
            return true;
        }
        // s前i个字符
        // p前j个字符
        // 是否匹配
        boolean[][] dp = new boolean[s.length() + 1][p.length() + 1];
        dp[0][0] = true;


        char[] chars1 = s.toCharArray();
        char[] chars2 = p.toCharArray();
        for (int i = 1; i < chars1.length; i++) {
            char c1 = chars1[i - 1];
            for (int j = 1; j < chars2.length; j++) {
                char c2 = chars2[j - 1];

                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // 
                else {
                    if (c2 == '.') {
                        dp[i][j] = dp[i - 1][j - 1];
                    } else if (c2 == '*') {
                        dp[i][j] =
                                dp[i][j - 1] || //匹配了1次
                                        dp[i][j - 2] || //匹配了0次
                                        dp[i - 1][j - 1] ||//匹配了0次
                                        dp[i - 1][j - 1] ||//匹配了0次
                                        dp[i - 1][j - 1] //匹配了0次
                        ;
                    } else {

                    }
                }


            }
        }


        return false;
    }


    public static void main(String[] args) {
        P10_RegExpMatching p = new P10_RegExpMatching();
        System.out.println(p.isMatch("aa", "a"));//false
        System.out.println(p.isMatch("aa", "a*"));//true
        System.out.println(p.isMatch("ab", ".*"));//true
        System.out.println(p.isMatch("aab", "c*a*b"));//true
        System.out.println(p.isMatch("mississippi", "mis*is*p*."));//false
    }

}
