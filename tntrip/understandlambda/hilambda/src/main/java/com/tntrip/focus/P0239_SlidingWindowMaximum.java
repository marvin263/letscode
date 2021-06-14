package com.tntrip.focus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author libin
 * @Date 2021/6/14
 */
public class P0239_SlidingWindowMaximum {
    private final HeapComparator hc = new BigRootHeapComparator();


    // heapIndex从1开始
    // arrayIndex算到heap上，其得到的heapIndex
    private int toHeapIndex(int arrayFromIndex, int arrayIndex) {
        return arrayIndex - (arrayFromIndex - 1);
    }

    // heapIndex从1开始
    // heapIndex回到array上，其得到的arrayIndex
    private int toArrayIndex(int arrayFromIndex, int heapIndex) {
        return heapIndex + (arrayFromIndex - 1);
    }

    /**
     * 数组最后一个元素，向上浮
     * <p>
     * 1-based array
     * <pre>
     *
     *     P: i
     *     L: 2*i
     *     R: (2*i)+1
     *
     *     L: x
     *     R: x+1
     *     P:  left/2 或 right/2
     *
     * </pre>
     * <p>
     *
     * @param array
     * @param arrayIndex
     */
    public void siftUp(int[] array, int arrayFromIndex, int arrayEndIndex, int arrayIndex) {
        // 上浮时，总是从整个heap的最后一个元素上浮。
        // 最后一个元素上浮，最终又再次 得到了 堆顶（最大或最小）
        int cIdx = toHeapIndex(arrayFromIndex, arrayIndex);
        // 1 就已经是根节点了，无需再搞
        while (cIdx > 1) {
            // P: idx/2
            int pIdx = cIdx / 2;
            // 根据 “堆有序” 调整 parent & child
            if (hc.cmp(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array)) {
                swap(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array);
                cIdx = pIdx;
            } else {
                break;
            }
        }
    }

    /**
     * 1号元素，向上沉
     * <p>
     * 1-based array
     * <pre>
     *
     *     P: i
     *     L: 2*i
     *     R: (2*i)+1
     *
     *     L: x
     *     R: x+1
     *     P:  left/2 或 right/2
     *
     * </pre>
     * <p>
     *
     * @param arrayIndex
     */
    public void siftDown(int[] array, int arrayFromIndex, int arrayEndIndex, int arrayIndex) {
        int hEndInclusive = toHeapIndex(arrayFromIndex, arrayEndIndex);

        int pIdx = toHeapIndex(arrayFromIndex, arrayIndex);
        int left = pIdx * 2;
        while (left <= hEndInclusive) {
            /*
             *        1
             *    20     50
             *
             *  1下沉时，和谁交换？
             * 和更大的交换才行，否则，交换完依然不是“堆有序”的
             * 50和1交换，很好，没问题；20和1交换，完犊子（依然不是“堆有序”）
             *
             */
            int cIdx;
            // 已经没有 后面的元素了
            if (left < hEndInclusive) {
                int right = left + 1;
                cIdx = hc.cmp(toArrayIndex(arrayFromIndex, left), toArrayIndex(arrayFromIndex, right), array) ? right : left;
            } else {
                cIdx = left;
            }

            // 根据 “堆有序” 调整 parent & child 
            if (hc.cmp(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array)) {
                swap(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array);
                pIdx = cIdx;
                left = pIdx * 2;
            } else {
                break;
            }
        }
    }

    public void buildHeap(int[] array, int arrayFromIndex, int arrayEndIndex) {
        int heapFromIndex = toHeapIndex(arrayFromIndex, arrayFromIndex);
        int heapEndIndex = toHeapIndex(arrayFromIndex, arrayEndIndex);

        for (int hIndex = (heapEndIndex) / 2; hIndex >= 1; hIndex--) {
            siftDown(array,
                    toArrayIndex(arrayFromIndex, heapFromIndex),
                    toArrayIndex(arrayFromIndex, heapEndIndex),
                    toArrayIndex(arrayFromIndex, hIndex));
        }
    }

    public void buildHeap2(int[] array, int arrayFromIndex, int arrayEndIndex) {
        // 一半的元素需要siftDown，所以是
        for (int arrayIndex = (arrayFromIndex + arrayEndIndex - 1) / 2; arrayIndex >= arrayFromIndex; arrayIndex--) {
            siftDown(array, arrayFromIndex, arrayEndIndex, arrayIndex);
        }
    }

    public void heapSort(int[] array, int arrayFromIndex, int arrayEndIndex) {
        buildHeap2(array, arrayFromIndex, arrayEndIndex);
        //buildHeap(array, arrayFromIndex, arrayEndIndex);

        for (int last = arrayEndIndex; last > arrayFromIndex; last--) {
            swap(arrayFromIndex, last, array);
            siftDown(array, arrayFromIndex, last - 1, arrayFromIndex);
        }
    }


    public static class BigRootHeapComparator implements HeapComparator {
        @Override
        public boolean cmp(int arrayIdx1, int arrayIdx2, int[] array) {
            return lessThan(arrayIdx1, arrayIdx2, array);
        }

        private boolean lessThan(int idx1, int idx2, int[] array) {
            return array[idx1] < array[idx2];
        }
    }

    public static class SmallRootHeapComparator implements HeapComparator {
        @Override
        public boolean cmp(int arrayIdx1, int arrayIdx2, int[] array) {
            return greaterThan(arrayIdx1, arrayIdx2, array);
        }

        private boolean greaterThan(int idx1, int idx2, int[] array) {
            return array[idx1] > array[idx2];
        }
    }

    public interface HeapComparator {
        boolean cmp(int arrayIdx1, int arrayIdx2, int[] array);
    }


    public int[] maxSlidingWindow(int[] nums, int k) {
        List<Integer> rst = new ArrayList<>();
        int left = 0;
        int right = k - 1;
        int[] working = new int[k];
        while (right < nums.length) {
            System.arraycopy(nums, left, working, 0, k);
            buildHeap(working, 0, working.length - 1);
            rst.add(working[0]);
            left++;
            right++;
        }
        return Arrays.stream(rst.toArray(new Integer[0])).mapToInt(e -> e).toArray();
    }

    public static void swap(int index1, int index2, int[] array) {
        int tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
    }


    public static void main(String[] args) {
        P0239_SlidingWindowMaximum p = new P0239_SlidingWindowMaximum();
        // [3,3,5,5,6,7]
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));
//
//        // [1]
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1}, 1)));
//
//        // [1,-1]
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1, -1}, 1)));
//
//        // [11]
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{9, 11}, 2)));
//
//        // [4]
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{4, -2}, 2)));
//
//        // [3,3,5,5,6,7]
//        
//        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));
        // [9,9,10,9,9,9,10,10,10,9,9,9,8,8]
        // [9,9,10,10,10,10,10,10,10,9,9,9,8,8]
        System.out.println(Arrays.toString(p.maxSlidingWindow(new int[]{-6, -10, -7, -1, -9, 9, -8, -4, 10, -5, 2, 9, 0, -7, 7, 4, -2, -10, 8, 7}, 7)));

    }
}
