package com.tntrip.mob.askq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nuc on 2017/11/5.
 */
public class LCS {
    private static final Logger LOG = LoggerFactory.getLogger(LCS.class);

    public enum IfIncrOneCharUseWho {
        EQUAL, USE_X, USE_Y
    }

    public static int[][] lcsLength(char[] x, char[] y, IfIncrOneCharUseWho[][] b) {
        int m = x.length-1;
        int n = y.length-1;
        int[][] c = new int[m + 1][n + 1];//此时，所有元素当然都是0
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                //1. x和y上字符一致
                if (x[i] == y[j]) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    b[i][j] = IfIncrOneCharUseWho.EQUAL;
                }
                //2. x和y上字符不一致，且，如果x或y少一个字符时：x少一个字符时，那时，公共子序列更长
                else if (c[i - 1][j] > c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    b[i][j] = IfIncrOneCharUseWho.USE_X;
                }
                // 3. y少一个字符时，那时，公共子序列更长
                else {
                    c[i][j] = c[i][j - 1];
                    b[i][j] = IfIncrOneCharUseWho.USE_Y;
                }
            }
        }
        return c;
    }

    public static void lcs(int i, int j, char[] x, IfIncrOneCharUseWho[][] b) {
        if (i == 0 || j == 0) {
            return;
        }
        if (b[i][j] == IfIncrOneCharUseWho.EQUAL) {
            lcs(i - 1, j - 1, x, b);
            System.out.println(x[i]);
        } else if (b[i][j] == IfIncrOneCharUseWho.USE_X) {
            lcs(i - 1, j, x, b);
        } else {
            lcs(i, j - 1, x, b);
        }
    }

    public static void main(String[] args) {
        char[] x = {' ', 'a', 'b', 'c', 'x', 'y', 'z', '1', '2', '3'};
        char[] y = {' ', '1', '2', '3', 'x', 'y', 'z', 'a', 'b', 'c'};
        IfIncrOneCharUseWho[][] b = new IfIncrOneCharUseWho[x.length][y.length];
        int[][] c = lcsLength(x, y, b);
        lcs(x.length-1, x.length-1, x, b);
    }
}
