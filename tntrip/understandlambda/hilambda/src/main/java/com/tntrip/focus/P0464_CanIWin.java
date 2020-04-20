package com.tntrip.focus;

public class P0464_CanIWin {
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        // 最大可选 i，目标是j时，先出手的玩家必赢dp[i][j]
        boolean[][] dp = new boolean[maxChoosableInteger + 1][desiredTotal + 1];
        for (int i = 1; i <= maxChoosableInteger; i++) {
            for (int j = 1; j <= i; j++) {
                dp[i][j] = true;
            }
        }

        return dp[maxChoosableInteger][desiredTotal];
    }

    public static void main(String[] args) {
        P0464_CanIWin p = new P0464_CanIWin();
        System.out.println(p.canIWin(10, 11));//false
    }

}
