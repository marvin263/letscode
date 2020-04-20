package com.basicsort.s04_merge;

import com.basicsort.AbstractBasicSort;

/**
 * <pre>
 *
 *   第 08 种：  归并排序
 *   属于 归并排序(merging)
 *   stable, best--nlogn, average--nlogn, worst--nlogn, memory--n
 *
 *   利用 Three Hungarians' Algorithm，可做到 高度并行(highly parallelizable)
 *   从而 时间复杂度优化到 logn
 *
 * </pre>
 *
 * @author marvin
 */
public class S_08_MergeSort_merging extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        return null;
    }

    public static void main(String[] args) {
        runTest(new S_08_MergeSort_merging());
    }
}
