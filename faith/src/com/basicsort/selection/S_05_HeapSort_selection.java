package com.basicsort.selection;

import java.util.Arrays;

import com.basicsort.AbstractBasicSort;
import com.util.CommUtil;

/**
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
 * </pre>
 * 
 * @author marvin
 * 
 */
public class S_05_HeapSort_selection extends AbstractBasicSort {

    @Override
    public int[] doSort(int start, int end, int[] array) {
        ascending_By_BigRoot(start, end, array);
        System.out.println("After ascending: " + Arrays.toString(array));
        descending_By_SmallRoot(start, end, array);
        System.out.println("After descending: " + Arrays.toString(array));
        return array;
    }

    public void ascending_By_BigRoot(int start, int end, int[] array) {
        buildBigRootHeap(start, end, array);
        int cur = end;
        while (cur > start) {
            CommUtil.swap(start, cur, array);
            cur = cur - 1;
            buildBigRootHeap(start, cur, array);
        }
    }

    private void buildBigRootHeap(int start, int end, int[] array) {
        int lastNonleaf = end / 2;
        for (int i = lastNonleaf; i >= start; i--) {
            adjustRoot(i, end, array, true);
        }
    }

    // Only start does not conform to heap definition
    // start's left and right subtree are both heap.
    private void adjustRoot(int start, int end, int[] array,
            boolean bNeedBigRoot) {
        int parent = start;
        while (parent <= end) {

            int left = 2 * parent;
            int right = left + 1;

            if (left <= end) {
                int child = -1;
                if (right <= end) {
                    // need big root
                    if (bNeedBigRoot) {
                        child = array[left] < array[right] ? right : left;
                    }
                    // need small root
                    else {
                        child = array[left] > array[right] ? right : left;
                    }
                } else {
                    child = left;
                }

                if (bNeedBigRoot) {
                    if (array[parent] < array[child]) {
                        CommUtil.swap(parent, child, array);
                        parent = child;
                    } else {
                        break;
                    }
                } else {
                    if (array[parent] > array[child]) {
                        CommUtil.swap(parent, child, array);
                        parent = child;
                    } else {
                        break;
                    }
                }

            } else {
                break;
            }
        }
        // System.out.println(Arrays.toString(array));
    }

    public void descending_By_SmallRoot(int start, int end, int[] array) {
        buildSmallRootHeap(start, end, array);
        int cur = end;
        while (cur > start) {
            CommUtil.swap(start, cur, array);
            cur = cur - 1;
            buildSmallRootHeap(start, cur, array);
        }
    }

    private void buildSmallRootHeap(int start, int end, int[] array) {
        int lastNonleaf = end / 2;
        for (int i = lastNonleaf; i >= start; i--) {
            adjustRoot(i, end, array, false);
        }
    }

    public static void main(String[] args) {
        int[] array = CommUtil.genereateRandomArray(10, 50);
        runTest(new S_05_HeapSort_selection(), 1, array.length - 1, array);
    }
}
