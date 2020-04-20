package com.basicsort.s03_insertion;

import com.basicsort.AbstractBasicSort;

/**
 * <pre>
 *
 *   第 07 种：  希尔排序
 *   属于 插入排序(insertion)
 *   unstable, best--nlogn, average--n^(4/3), worst--n^(4/3), memory--1
 *
 * </pre>
 * 
 * @author marvin
 * 
 */
public class S_07_ShellSort_insertion extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        straighInsertSort(start, end, array);
        return array;
    }

    private void straighInsertSort(int start, int end, int[] array) {
        for (int i = start + 1; i <= end; i++) {
            int curValue = array[i];
            int newIndex = start;
            for (int s = i - 1; s >= start; s--) {
                if (curValue < array[s]) {
                    array[s + 1] = array[s];// (s+1) shifts back
                } else {
                    newIndex = s + 1;// curValue should fall into (s+1)
                    break;
                }
            }
            array[newIndex] = curValue;
        }
    }

    public static void main(String[] args) {
        int[] array = new int[] { 35, 2, 47, 10, 4, 23, 10, 12, 1, 32 };
        runTest(new S_07_ShellSort_insertion());
    }
}
