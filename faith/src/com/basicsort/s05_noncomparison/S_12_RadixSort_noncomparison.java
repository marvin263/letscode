package com.basicsort.s05_noncomparison;

import com.basicsort.AbstractBasicSort;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   第 11 种：  基数排序（radix sort）
 *   属于 非比较 排序(non comparison)
 *   
 *   stable, best--xxx, average--n*(k/d), worst--n*(k/d), memory--n+2^d, n<<2^k--no
 *   
 *   MSD--从高位开始排序
 *   LSD--从低位开始排序
 * 
 * 
 *  基数排序 vs 计数排序 vs 桶排序
 *
 *  这三种排序算法都利用了桶的概念，但对桶的使用方法上有明显差异：
 *     1. 计数排序：每个桶只存储单一键值
 *     2. 桶排序：每个桶存储一定范围的数值
 *     3. 基数排序：根据键值的每位数字来分配桶
 * 
 * </pre>
 *
 * @author marvin
 */
public class S_12_RadixSort_noncomparison extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        return null;
    }

    /**
     * 基数排序
     *
     * @param array
     * @return
     */
    public static int[] RadixSort(int[] array) {
        if (array == null || array.length < 2)
            return array;
        // 1.先算出最大数的位数；
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i]);
        }
        int maxDigit = 0;
        while (max != 0) {
            max /= 10;
            maxDigit++;
        }
        int mod = 10, div = 1;
        ArrayList<ArrayList<Integer>> bucketList = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 10; i++)
            bucketList.add(new ArrayList<Integer>());
        for (int i = 0; i < maxDigit; i++, mod *= 10, div *= 10) {
            for (int j = 0; j < array.length; j++) {
                int num = (array[j] % mod) / div;
                bucketList.get(num).add(array[j]);
            }
            int index = 0;
            for (int j = 0; j < bucketList.size(); j++) {
                for (int k = 0; k < bucketList.get(j).size(); k++)
                    array[index++] = bucketList.get(j).get(k);
                bucketList.get(j).clear();
            }
        }
        return array;
    }

    public static void main(String[] args) {
        runTest(new S_12_RadixSort_noncomparison());
    }
}
