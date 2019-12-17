package com.tntrip;

import java.util.Arrays;

public class P338_CountingBits {
    public int[] countBits(int num) {
        int[] array = new int[num + 1];
        for (int n = 1; n <= num; n++) {
            // n/2 这个数的里面，‘1’的个数已经知道
            // 右移一位，如果移动掉了1，就再加回来；并没有移动掉1，就加0
            array[n] = array[n >> 1] + (n & 0x01);
        }
        return array;
    }

    public static void main(String[] args) {
        P338_CountingBits p = new P338_CountingBits();
        System.out.println(Arrays.toString(p.countBits(500)));
    }
}
