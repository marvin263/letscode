package com.search;

import com.util.CommUtil;

import java.util.Arrays;

public abstract class AbstractBasicSearch {
    public abstract int[] doSort(int start, int end, int[] array);

    public void testSort(int start, int end, int[] array) {
        System.out.println("Before: start=" + start + ", end=" + end + "--"
                + Arrays.toString(array));
        
        int[] returnedArray = doSort(start, end, array);
        System.out.println(" After: start=" + start + ", end=" + end + "--"
                + Arrays.toString(returnedArray));
    }

    public static void runTest(AbstractBasicSearch abs) {
        int[] array = CommUtil.genereateRandomArray(10, 50);
        abs.testSort(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSearch abs, int[] array) {
        abs.testSort(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSearch abs, int start, int end,
                               int[] array) {
        abs.testSort(start, end, array);
    }

}
