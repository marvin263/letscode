package com.basicsort.s04_merge;

import com.basicsort.AbstractBasicSort;

/**
 * <pre>
 *
 *   第 09 种：  四路排序
 *   属于 归并排序(merging)
 *   stable, best--n, average--nlogn, worst--nlogn, memory--n
 *
 *   Use a 4-input sorting network
 *
 * </pre>
 *
 * @author marvin
 */
public class S_09_QuadSort_merging extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        return null;
    }

    public static void main(String[] args) {
        runTest(new S_09_QuadSort_merging());
    }
}
