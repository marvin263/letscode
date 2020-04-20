package com.basicsort.s02_selection;

import com.basicsort.AbstractBasicSort;
import com.util.CommUtil;

/**
 * <pre>
 *
 *   第 05 种：  树选择排序
 *   属于 选择排序(selection)
 *   unstable, best--n^2, average--n^2, worst--n^2, memory--1
 *
 * </pre>
 * 
 * @author marvin
 * 
 */
public class S_05_TreeSelectionSort_selection extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        simpleSelectionSort(start, end, array);
        return array;
    }

    /**
     * 1. auxiliary space-------- Only a tmp int, used for swap
     * 
     */
    public void simpleSelectionSort(int start, int end, int[] array) {
        for (int i = start; i <= end - 1; i++) {
            int minIndex = i;
            for (int k = i + 1; k <= end; k++) {
                if (array[k] < array[minIndex]) {
                    minIndex = k;
                }
            }
            // Yes, find smaller element
            if (minIndex != i) {
                CommUtil.swap(i, minIndex, array);
            }
        }
    }

    public static void main(String[] args) {
        runTest(new S_05_TreeSelectionSort_selection());
    }
}
