package com.basicsort.s04_merge;

import com.basicsort.AbstractBasicSort;

import java.util.Arrays;

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
        return mergeSort(Arrays.copyOfRange(array, 0, array.length));
    }

    /**
     * 归并排序
     *
     * @param array
     * @return
     */
    public static int[] mergeSort(int[] array) {
        if (array.length < 2) return array;
        int mid = array.length / 2;
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);
        return merge(mergeSort(left), mergeSort(right));
    }

    /**
     * 归并排序——将两段排序好的数组结合成一个排序数组
     *
     * @param left
     * @param right
     * @return
     */
    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        for (int index = 0, i = 0, j = 0; index < result.length; index++) {
            if (i >= left.length)
                result[index] = right[j++];
            else if (j >= right.length)
                result[index] = left[i++];
            else if (left[i] > right[j])
                result[index] = right[j++];
            else
                result[index] = left[i++];
        }
        return result;
    }

    public static void main(String[] args) {
        runTest(new S_08_MergeSort_merging());
    }
}
