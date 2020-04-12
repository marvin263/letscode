package com.tntrip.focus;

import java.math.BigInteger;
import java.util.Arrays;

public class P0887_SuperEggDrop {
    /**
     * firstly, if we have k eggs and s steps to detect a building with Q(k, s) floors,
     * <p>
     * secondly, we use 1 egg and 1 step to detect one floor,
     * if egg break, we can use (k-1) eggs and (s-1) step to detect with Q(k-1, s-1),
     * <p>
     * if egg isn't broken, we can use k eggs and (s-1) step to detect with Q(k, s-1),
     * So, Q(k, s) = 1 + Q(k, s-1) + Q(k-1, s-1);
     * <p>
     * dp[i] is max floors we can use i eggs and s step to detect.
     *
     * @param K
     * @param N
     * @return
     */
    public int superEggDrop_2(int K, int N) {
        int[] dp = new int[K + 1];
        int step = 0;
        for (; dp[K] < N; step++) {
            for (int i = K; i > 0; i--)
                dp[i] = (1 + dp[i] + dp[i - 1]);
        }
        return step;
    }

    public int superEggDrop_recursive(int K, int N) {
        int[][] result = new int[K + 1][N + 1];
        for (int[] row : result) {
            Arrays.fill(row, -1);
        }

        // 只有1个蛋，n层则需要n次
        for (int n = 0; n <= N; n++) {
            result[1][n] = n;
        }
        // 只有1层，无论几个蛋，总是需要1次
        for (int k = 0; k <= K; k++) {
            result[k][1] = 1;
        }

        // 只有0个蛋，总是需要0次
        Arrays.fill(result[0], 0);
        // 只有0层，总是需要0次
        for (int k = 0; k <= K; k++) {
            result[k][0] = 0;
        }

        return dp(result, K, N);
    }


    public int dp(int[][] result, int K, int N) {
        if (result[K][N] != -1) {
            return result[K][N];
        }

        int finalRst = Integer.MAX_VALUE;
        for (int n = 1; n <= N; n++) {
            finalRst = Math.min(
                    finalRst,
                    Math.max(dp(result, K - 1, n - 1), dp(result, K, N - n)) + 1
            );
        }
        result[K][N] = finalRst;
        //System.out.println("K=" + K + ", N=" + N + ", finalRst=" + finalRst);
        return finalRst;
    }

    /**
     * 计算排列(permutation)的数量
     * <p>
     * n>0，且，0<= m <= n
     *
     * @param n
     * @param m
     * @return
     */
    public static int p(int n, int m) {
        if (!(n >= 0)) {
            throw new RuntimeException("Require: n>=0. Actual n=" + m + ", m=" + m);
        }
        if (!(m >= 0 && m <= n)) {
            throw new RuntimeException("Require: 0 <= m <= n. Actual n=" + m + ", m=" + m);
        }

        if (n == 0) {
            return 0;
        }
        if (m == 0) {
            return 1;
        }

        int k = n - m;
        BigInteger d = BigInteger.valueOf(1);
        for (long i = n; i > k; i--) {
            d = d.multiply(BigInteger.valueOf(i));
        }

        // p(n, m)的实际值
        // d > Integer.MAX_VALUE
        if (d.max(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)).equals(d)) {
            throw new RuntimeException("permutationCount > Integer.MAX_VALUE. Actual n=" + n + ", m=" + m + ", permutationCount=" + d);
        }

        // n! / ( (n-m)! )
        int r = d.intValue();
        //System.out.println("n=" + n + ", m=" + m + ", permutationCount=" + r);
        return r;
    }


    /**
     * 计算组合(combination)的数量
     * <p>
     * n>0，且，0<= m <= n
     *
     * @param n
     * @param m
     * @return
     */
    public static int c(int n, int m) {
        if (!(n >= 0)) {
            throw new RuntimeException("Require: n>=0. Actual n=" + m + ", m=" + m);
        }
        if (!(m >= 0 && m <= n)) {
            throw new RuntimeException("Require: 0 <= m <= n. Actual n=" + m + ", m=" + m);
        }

        if (n == 0) {
            return 0;
        }

        if (m == 0 || m == n) {
            return 1;
        }

        long max = Math.max(m, (n - m));
        BigInteger d1 = BigInteger.valueOf(1);
        for (long i = n; i > max; i--) {
            d1 = d1.multiply(BigInteger.valueOf(i));
        }

        long min = Math.min(m, (n - m));
        BigInteger d2 = BigInteger.valueOf(1);
        for (long i = min; i > 0; i--) {
            d2 = d2.multiply(BigInteger.valueOf(i));
        }
        // c(n, m)的实际值
        BigInteger d = d1.divide(d2);
        // d > Integer.MAX_VALUE
        if (d.max(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)).equals(d)) {
            throw new RuntimeException("combinationCount > Integer.MAX_VALUE. Actual n=" + n + ", m=" + m + ", combinationCount=" + d);
        }
        // n!/(m! (n-m)!)
        int r = d.intValue();
        //System.out.println("n=" + n + ", m=" + m + ", combinationCount=" + r);
        return r;
    }

    /**
     * h：Full BST时的高度，1-based。也就是层的数量，每一层算作高度1
     * <p>
     * c：列的索引，0-based。最左侧是第0列，最右侧的列的索引是 (h-1)
     *
     * @param h
     * @param c
     * @return
     */
    public static int nodeCountOfColumn(int h, int c) {
        // (h)!/( c! (h-c)! )
        // c(h, c)
        if (!(h > 0)) {
            throw new RuntimeException("nodeCountOfColumn requires: h>0. Actual h=" + h + ", c=" + c);
        }
        int r = c(h, c);
        //System.out.println("height=" + h + ", col=" + c + ", nodeCount=" + r);
        return r;
    }

    /**
     * h：Full BST时的高度，1-based。也就是层的数量，每一层算作高度1
     * <p>
     * c：列的索引，0-based。最左侧是第0列，最右侧的列的索引是 (h-1)
     *
     * @param h
     * @param c
     * @return
     */
    public static int leafCountOfColumn(int h, int c) {
        // h' = h-1
        // (h')!/( c! (h'-c)! )
        // c(h', c)
        if (!(h > 0)) {
            throw new RuntimeException("leafCountOfColumn requires: h>0. Actual h=" + h + ", c=" + c);
        }
        if (h == 1) {
            return 1;
        }
        int r = c((h - 1), c);
        //System.out.println("height=" + h + ", col=" + c + ", leafCount=" + r);
        return r;
    }


    public int superEggDrop(int K, int N) {
        if (K == 1) {
            return N;
        }
        int nextFullBSTHeight = (int) (Math.ceil(log(N + 1, 2)));
        int width = nextFullBSTHeight;
        int height = nextFullBSTHeight;
        // 蛋足够多
        if (K >= width) {
            return height;
        }

        // 0-based col
        int rightMostCol = width - 1;
        int leftMostCol = width - K;

        int nodeCountOnCol = 0;
        int[] leavesOnEachCol = new int[width];
        for (int col = leftMostCol; col <= rightMostCol; col++) {
            nodeCountOnCol += nodeCountOfColumn(height, col);
            leavesOnEachCol[col] = leafCountOfColumn(height, col);
        }
        int supplementaryCount = N - nodeCountOnCol;

        int minSteps = 0;
        int round = 0;
        if (supplementaryCount > 0) {
            int alreadyDone = 0;
            while (alreadyDone < supplementaryCount) {
                for (int col = leftMostCol; col <= rightMostCol; col++) {
                    alreadyDone += (col == leftMostCol) ? leavesOnEachCol[col] : 2 * leavesOnEachCol[col];
                    leavesOnEachCol[col] = (col == rightMostCol) ? leavesOnEachCol[col] : (leavesOnEachCol[col] + leavesOnEachCol[col + 1]);
                }
                round++;
            }
            minSteps = height + round;
        }
        // 无需补充
        else {
            minSteps = height;
        }

        System.out.println("K=" + K + ", N=" + N + ", supplementaryCount=" + supplementaryCount + ", round=" + round + ", minSteps=" + minSteps);
        return minSteps;
    }

    public static double log(double d, double based) {
        return Math.log10(d) / Math.log10(based);
    }

    public static void main(String[] args) {
        P0887_SuperEggDrop p = new P0887_SuperEggDrop();
        p.superEggDrop(2, 1);
    }
}
