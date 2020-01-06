package com.tntrip;

import java.util.ArrayList;
import java.util.List;

public class P1143_LongestCommonSubsequence2 {
    public static class EachStep {
        private final int curLCSLength;
        private List<Point> prevPoints = new ArrayList<>();

        public void addFrom(int fromI, int fromJ) {
            prevPoints.add(Point.create(fromI, fromJ));
        }

        private EachStep(final int curLCSLength) {
            this.curLCSLength = curLCSLength;
        }

        public static EachStep create(final int curLCSLength) {
            return new EachStep(curLCSLength);
        }

        public static EachStep create(final int curLCSLength, int fromI, int fromJ) {
            EachStep es = new EachStep(curLCSLength);
            es.addFrom(fromI, fromJ);
            return es;
        }

        public static class Point {
            private final int i;
            private final int j;

            private Point(final int i, final int j) {
                this.i = i;
                this.j = j;
            }

            public static Point create(final int i, final int j) {
                return new Point(i, j);
            }
        }
    }

    public EachStep[][] longestCommonSubsequence(String text1, String text2) {
        // 前i字符
        // 前j字符
        // 最长公共子序列 的长度
        EachStep[][] dp = new EachStep[text1.length() + 1][text2.length() + 1];
        char[] chars1 = text1.toCharArray();
        char[] chars2 = text2.toCharArray();

        for (int i = 1; i <= chars1.length; i++) {
            char c1 = chars1[i - 1];
            for (int j = 1; j <= chars2.length; j++) {
                char c2 = chars2[j - 1];
                if (c1 == c2) {
                    dp[i][j] = EachStep.create(dp[i - 1][j - 1].curLCSLength + 1, (i - 1), (j - 1));
                } else {
                    dp[i][j] = chooseMax(dp, i, j - 1, i - 1, j);
                }
            }
        }
        return dp;
    }

    private void fdfdfd(EachStep[][] dp, int i, int j) {
        EachStep es = dp[i][j];
        StringBuilder sb = new StringBuilder();
        //sb.append()

    }

    private EachStep chooseMax(EachStep[][] dp, int fromI1, int fromJ1, int fromI2, int fromJ2) {
        // >
        if (dp[fromI1][fromJ1].curLCSLength > dp[fromI2][fromJ2].curLCSLength) {
            return EachStep.create(dp[fromI1][fromJ1].curLCSLength, (fromI1), (fromJ1));
        }
        // <
        else if (dp[fromI1][fromJ1].curLCSLength < dp[fromI2][fromJ2].curLCSLength) {
            return EachStep.create(dp[fromI2][fromJ2].curLCSLength, (fromI2), (fromJ2));
        }
        // 相等
        else {
            EachStep es = EachStep.create(dp[fromI1][fromJ1].curLCSLength);
            es.addFrom(fromI1, fromJ1);
            es.addFrom(fromI2, fromJ2);
            return es;
        }
    }

    public static void main(String[] args) {
        P1143_LongestCommonSubsequence2 p = new P1143_LongestCommonSubsequence2();
        System.out.println(p.longestCommonSubsequence("abcde", "ace"));//3
        System.out.println(p.longestCommonSubsequence("abc", "abc"));//3
        System.out.println(p.longestCommonSubsequence("abc", "def"));//0
    }

}
