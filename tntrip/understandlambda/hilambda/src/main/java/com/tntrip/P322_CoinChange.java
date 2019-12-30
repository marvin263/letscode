package com.tntrip;

import java.util.Arrays;

public class P322_CoinChange {
    public static final int NOT_EXIST = -1;

    public int coinChange(int[] coins, int amount) {
        // 金额为i时，所使用的全部硬币的数量
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, NOT_EXIST);
        // 金额为0时，需要0个硬币
        dp[0] = 0;
        // 每一个金额为i
        for (int i = 1; i <= amount; i++) {
            // 每一个单个硬币的金额
            for (int c : coins) {
                // 当前金额 >= 单个给定硬币
                // 对于金额i，“使用” 和 “不使用” 该硬币c 时，所使用的全部硬币的数量
                if (i >= c) {
                    // 金额为 i-c 时，无论怎么组合硬币，都得不出该金额
                    if (dp[i - c] == NOT_EXIST) {

                    }
                    // 金额为 i-c 时，可以组合出硬币
                    else {
                        if (dp[i] == NOT_EXIST) {
                            dp[i] = dp[i - c] + 1;
                        } else {
                            dp[i] = Math.min(dp[i], dp[i - c] + 1);
                        }
                    }
                }
            }
        }
        return dp[amount];
    }

    public static void main(String[] args) {
        P322_CoinChange p = new P322_CoinChange();

        System.out.println(p.coinChange(new int[]{1, 2, 5}, 11));//3
        System.out.println(p.coinChange(new int[]{2}, 3));//-1
    }
}
