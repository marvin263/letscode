package com.tntrip.interview;

import java.util.Arrays;

/**
 * Created by libing2 on 2016/10/11.
 */
public class BubbleSortAnswer {
    public static int[] bubbleSort(int start, int end, int[] array) {
        for (int i = start; i <= end - 1; i++) {
            for (int k = i + 1; k <= end; k++) {
                if (array[i] > array[k]) {
                    int tmp = array[i];
                    array[i] = array[k];
                    array[k] = tmp;
                }
            }
        }
        return array;
    }

    public static void main(String[] args) {
        int[] array = new int[]{5, 1, 3, 9, 9, 6, 3, 23, 5};

        System.out.println("Before sort: " + Arrays.toString(array));
        bubbleSort(0, array.length - 1, array);
        System.out.println("After sort: " + Arrays.toString(array));
    }
}
