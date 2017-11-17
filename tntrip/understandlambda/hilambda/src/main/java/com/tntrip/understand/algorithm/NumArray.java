package com.tntrip.understand.algorithm;

/**
 * Created by nuc on 2016/9/14.
 */
public class NumArray {
    private int[] sums;

    public NumArray(int[] nums) {
        sums = new int[nums.length + 1];
        sums[0] = 0;
        for (int i = 0; i < nums.length; i++) {
            sums[i + 1] = sums[i] + nums[i];
        }
    }

    public int sumRange(int i, int j) {
        return sums[j + 1] - sums[i];
    }

    public static void main(String[] args) {
        NumArray na = new NumArray(new int[]{-2, 0, 3, -5, 2, -1});
        na.sumRange(0, 2);
        na.sumRange(2, 5);
        na.sumRange(0, 5);
    }
}


// Your NumArray object will be instantiated and called as such:
// NumArray numArray = new NumArray(nums);na.
// numArray.sumRange(0, 1);na.
// numArray.sumRange(1, 2);na.
