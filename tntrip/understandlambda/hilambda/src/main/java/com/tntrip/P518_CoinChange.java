package com.tntrip;

import java.util.Arrays;

public class P518_CoinChange {
    public static final int NOT_EXIST = -1;

    public int change(int amount, int[] coins) {
        // 金额为i时，硬币组合方式 的数量
        int[] dp = new int[amount + 1];
        // 金额为0时，硬币组合方式 是 NOT_EXIST
        Arrays.fill(dp, NOT_EXIST);

        // 注意，每一个硬币都是不一样的
        // 当只使用硬币c时，组合情况
        for (int c : coins) {
            for (int j = 1; j <= amount; j++) {
                // 金额 和 该硬币c 相等
                if (j - c == 0) {
                    // dp[j-c]就是dp[0]，它的值永远都是 NOT_EXIST
                    if (dp[j] == NOT_EXIST) {
                        dp[j] = 1;
                    } else {
                        dp[j] += 1;
                    }
                }
                // 不使用 该硬币c 时，即 (j-c)
                else if (j - c > 0) {
                    if (dp[j] == NOT_EXIST) {
                        // 原来的次数，即使当前 dp[j]的次数
                        dp[j] = (dp[j - c] == NOT_EXIST) ? 0 : dp[j - c];
                    } else {
                        // 对于金额i，  原来的次数dp[j](不使用硬币c)，再加上
                        // 对于金额i-c，原来次数dp[j-c](不使用硬币c)，每一个组合给其加一个硬币c 得到的金额就是 j
                        dp[j] += (dp[j - c] == NOT_EXIST) ? 0 : dp[j - c];
                    }
                }
                // do nothing
                else {

                }
            }
        }
        return Math.max(dp[amount], 0);
    }


    public int cddhange(int amount, int[] coins) {
        // 前i个币，金额j，硬币组合的 个数
        int[][] dp = new int[coins.length + 1][amount + 1];
        for (int i = 0; i < dp.length; i++) {
            Arrays.fill(dp[i], NOT_EXIST);
        }

        for (int i = 1; i <= coins.length; i++) {
            // 每一个硬币
            int c = coins[i - 1];
            for (int j = 1; j <= amount; j++) {
                // 金额 和 该硬币c 相等
                if (j - c == 0) {
                    if (dp[i][j] == NOT_EXIST) {
                        dp[i][j] = 1;
                    }
                    // 使用前i个硬币，在使用硬币c前，已经可以得到总金额j啦，所以，而，这次硬币c也能得到，所以，需要加1
                    else {
                        dp[i][j] += (dp[i][j] == NOT_EXIST) ? 0 : dp[i][j];
                    }
                }
                // 不使用 该硬币c 时，即 (j-c)
                else if (j - c > 0) {
                    if (dp[i][j] == NOT_EXIST) {
                        dp[i][j] = (dp[i][j - c] == NOT_EXIST && dp[i][j] == NOT_EXIST)
                                ? NOT_EXIST :
                                (
                                        (dp[i][j - c] == NOT_EXIST ? 0 : dp[i][j - c]) +
                                                (dp[i][j] == NOT_EXIST ? 0 : dp[i][j])
                                );
                    } else {
                        dp[i][j] = (dp[i][j - c] == NOT_EXIST && dp[i][j] == NOT_EXIST)
                                ? NOT_EXIST :
                                (
                                        (dp[i][j - c] == NOT_EXIST ? 0 : dp[i][j - c]) +
                                                (dp[i][j] == NOT_EXIST ? 0 : dp[i][j])
                                );
                    }
                }
                // do nothing
                else {
                    dp[i][j] = dp[i][j];
                }
            }
        }
        return Math.max(dp[coins.length][amount], 0);
    }


    public static void main(String[] args) {
        P518_CoinChange p = new P518_CoinChange();

        System.out.println(p.change(5, new int[]{1, 2, 5}));//4
        System.out.println(p.change(3, new int[]{2}));//0
        System.out.println(p.change(10, new int[]{10}));//1

        System.out.println(p.cddhange(5, new int[]{1, 2, 5}));//4
        System.out.println(p.cddhange(3, new int[]{2}));//0
        System.out.println(p.cddhange(10, new int[]{10}));//1
    }
}
