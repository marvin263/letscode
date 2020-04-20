package com.basicsort.s01_exchange;

import com.basicsort.AbstractBasicSort;

/**
 * https://www.cnblogs.com/guoyaohua/p/8600214.html
 * <p>
 * https://en.wikipedia.org/wiki/Sorting_algorithm
 * <p>
 * https://zh.wikipedia.org/wiki/%E6%8E%92%E5%BA%8F%E7%AE%97%E6%B3%95
 *
 * <pre>
 *
 *   第 01 种：  冒泡排序
 *   交换排序(exchange)
 *   stable, best--n, average--n^2, worst--n^2, memory--1
 *
 * </pre>
 *
 * @author marvin
 */
public class S_01_BubbleSort_exchange extends AbstractBasicSort {

    /**
     * 1. need NEW array-----------NO
     * <p>
     * 2. auxiliary space-------- Only a tmp int, used for swap
     */
    @Override
    public int[] doSort(int start, int end, int[] array) {
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
        runTest(new S_01_BubbleSort_exchange());
    }
}
