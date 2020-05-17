package com.search;

import com.util.CommUtil;

import java.util.Arrays;

public abstract class AbstractBasicSearch {
    public abstract int doSearch(int start, int end, int[] array);

    public void testSearch(int start, int end, int[] array) {

        System.out.println("Before sort: start=" + start + ", end=" + end + "--"
                + Arrays.toString(array));
        Arrays.sort(array, start, end + 1);
        System.out.println("After sort: start=" + start + ", end=" + end + "--"
                + Arrays.toString(array));

        int returnedArray = doSearch(start, end, array);
        System.out.println("found key: start=" + start + ", end=" + end + "-- found key at: " + returnedArray);
    }

    public static void runTest(AbstractBasicSearch abs) {
        int[] array = CommUtil.genereateRandomArray(10, 50);
        abs.testSearch(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSearch abs, int[] array) {
        abs.testSearch(0, array.length - 1, array);
    }

    public static void runTest(AbstractBasicSearch abs, int start, int end,
                               int[] array) {
        abs.testSearch(start, end, array);
    }

}
