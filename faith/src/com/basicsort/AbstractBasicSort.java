package com.basicsort;

import java.util.Arrays;

import com.util.CommUtil;

public abstract class AbstractBasicSort {
    public abstract int[] doSort(int start, int end, int[] array);

    public void testSort(int start, int end, int[] array) {
        System.out.println("Before: start=" + start + ", end=" + end + "--"
                + Arrays.toString(array));
        
        int[] returnedArray = doSort(start, end, array);
        System.out.println(" After: start=" + start + ", end=" + end + "--"
                + Arrays.toString(returnedArray));
    }

    public static void runTest(AbstractBasicSort abs) {
        int[] array = CommUtil.genereateRandomArray(10, 50);
        abs.testSort(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSort abs, int[] array) {
        abs.testSort(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSort abs, int start, int end,
            int[] array) {
        abs.testSort(start, end, array);
    }

}
