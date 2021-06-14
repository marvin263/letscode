package com.tntrip.focus;

import java.util.Arrays;

/**
 * @Author libin
 * @Date 2021/6/14
 */
public class P0239_SlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        return null;
    }

    public static void main(String[] args) {
        P0239_SlidingWindowMaximum p = new P0239_SlidingWindowMaximum();
        // [3,3,5,5,6,7]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));

        // [1]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1}, 1)));

        // [1,-1]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1, -1}, 1)));

        // [11]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{9, 11}, 2)));

        // [4]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{4, -2}, 2)));

    }
}
