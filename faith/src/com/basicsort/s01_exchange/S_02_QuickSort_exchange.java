package com.basicsort.s01_exchange;

import com.basicsort.AbstractBasicSort;

/**
 * <pre>
 *
 *   第 02 种：  快速排序
 *   交换排序(exchange)
 *   unstable, best--nlogn, average--nlogn, worst--n^2, memory--logn
 *
 * </pre>
 *
 * @author marvin
 */
public class S_02_QuickSort_exchange extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        quickSort(start, end, array);
        return array;
    }

    public void quickSort(int start, int end, int[] array) {
        if (start >= end) {
            return;
        }
        int pivot = firstElement_Should_Fall_Into(start, end, array);
        quickSort(start, pivot - 1, array);
        quickSort(pivot + 1, end, array);
    }

    private int firstElement_Should_Fall_Into(int low, int high, int[] array) {
        int firstValue = array[low];
        while (low < high) {
            while (low < high && array[high] >= firstValue) {
                high--;
            }
            array[low] = array[high];

            while (low < high && array[low] <= firstValue) {
                low++;
            }
            array[high] = array[low];
        }
        array[low] = firstValue;
        System.out.println("low=" + low + ", high=" + high
                + ". First element falls into: " + low);
        return low;
    }

    public static void main(String[] args) {
        runTest(new S_02_QuickSort_exchange());
    }
}
