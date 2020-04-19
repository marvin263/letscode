package com.basicsort.exchange;

import com.basicsort.AbstractBasicSort;

/**
 * https://www.cnblogs.com/guoyaohua/p/8600214.html
 * 
 * https://en.wikipedia.org/wiki/Sorting_algorithm
 * 
 * https://zh.wikipedia.org/wiki/%E6%8E%92%E5%BA%8F%E7%AE%97%E6%B3%95
 * 
 * <pre>
 * 稳定性：稳定
 * 
 * 空间复杂度，辅助空间：O(1)
 * 
 * 时间复杂度
 *          最坏：O(n^2)
 * 
 *          最好：O(n)
 * 
 *          平均：O(n^2)
 *          
 * 排序算法	复杂度	最坏时间复杂度	空间复杂度	是否稳定
 * 冒泡排序	平均时间O（n2）， 最坏O（n2），空间O（1）	是
 * </pre>
 * 
 * @author marvin
 * 
 */
public class S_01_BubbleSort_exchange extends AbstractBasicSort {

    /**
     * 
     * 1. need NEW array-----------NO
     * <p>
     * 2. auxiliary space-------- Only a tmp int, used for swap
     * 
     * 
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
