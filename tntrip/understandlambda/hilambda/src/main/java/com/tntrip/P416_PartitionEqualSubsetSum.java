package com.tntrip;


public class P416_PartitionEqualSubsetSum {

    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int n : nums) {
            sum += n;
        }
        if ((sum % 2) != 0) {
            return false;
        }
        int target = sum >> 1;
        // 前i个元素
        // 全选或部分选，加和为 j
        // 是否可以满足
        boolean[][] dp = new boolean[nums.length + 1][target + 1];

        for (int k = 0; k < nums.length; k++) {
            int i = k + 1;
            for (int j = 1; j <= target; j++) {
                if (j - nums[k] == 0) {
                    dp[i][j] = true;
                } else if (j - nums[k] > 0) {
                    dp[i][j] =
                            // 前(i-1)个元素时，是否 已经可以 加和为j
                            dp[i - 1][j]
                                    // 前(i-1)个元素时，加和不是j，但，已经是 j-nums[k] 啦        
                                    || dp[i - 1][j - nums[k]];
                } else {
                    // 本次的数 nums[k]，太大了，比整个加和都大。所以，看前面已经得到的结果吧 
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[nums.length][target];
    }


    public boolean canPartition2(int[] nums) {
        int sum = 0;
        for (int n : nums) {
            sum += n;
        }
        if ((sum % 2) != 0) {
            return false;
        }
        int target = sum >> 1;
        // 加和j
        // 全选或部分选 整个nums
        // 是否可以满足
        boolean[] dp = new boolean[target + 1];

        for (int n : nums) {
            for (int j = target; j >= 1; j--) {
                // 该加和j，肯定是可以满足的
                if (j - n == 0) {
                    dp[j] = true;
                } else if (j - n > 0) {
                    dp[j] =
                            // 仅仅使用nums之前的元素，是否就已经得到了 加和j
                            dp[j]
                                    // 仅仅使用nums之前的元素，是否就已经得到了 加和 j-n
                                    || dp[j - n];
                } else {
                    // n太大了，比整个加和都大。所以，看前面已经得到的结果吧 
                    dp[j] = dp[j];
                }
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        P416_PartitionEqualSubsetSum p = new P416_PartitionEqualSubsetSum();

        System.out.println(p.canPartition2(new int[]{1, 2, 5}));//false
        System.out.println(p.canPartition2(new int[]{1, 5, 11, 5}));//true
        System.out.println(p.canPartition2(new int[]{1, 2, 3, 5}));//false
    }
}
