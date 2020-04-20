package com.basicsort.s05_noncomparison;

import com.basicsort.AbstractBasicSort;

import java.util.Arrays;

/**
 * <pre>
 *
 *   第 10 种：计数排序（counting sort）
 *   属于 非比较 排序(non comparison)
 *   stable, best--xxx, average--n+r, worst--n+r, memory--n+r, n<<2^k--yes
 *
 * </pre>
 *
 * @author marvin
 */
public class S_10_CountingSort_noncomparison extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        return countingSort(Arrays.copyOfRange(array, 0, array.length));
    }

    /**
     * 计数排序
     *
     * @param array
     * @return
     */
    public static int[] countingSort(int[] array) {
        if (array.length == 0) return array;
        int bias, min = array[0], max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max)
                max = array[i];
            if (array[i] < min)
                min = array[i];
        }
        bias = 0 - min;
        int[] bucket = new int[max - min + 1];
        Arrays.fill(bucket, 0);
        for (int i = 0; i < array.length; i++) {
            bucket[array[i] + bias]++;
        }
        int index = 0, i = 0;
        while (index < array.length) {
            if (bucket[i] != 0) {
                array[index] = i - bias;
                bucket[i]--;
                index++;
            } else
                i++;
        }
        return array;
    }


    public static void main(String[] args) {
        runTest(new S_10_CountingSort_noncomparison());
    }
}
