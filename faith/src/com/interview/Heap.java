package com.interview;

import com.util.CommUtil;

import java.util.Arrays;

public class Heap {
    private final HeapComparator hc;

    public Heap(HeapComparator hc) {
        this.hc = hc;
    }

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
                CommUtil.swap(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array);
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
                CommUtil.swap(toArrayIndex(arrayFromIndex, pIdx), toArrayIndex(arrayFromIndex, cIdx), array);
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
            CommUtil.swap(arrayFromIndex, last, array);
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


    public static void main(String[] args) {
        int[] array = {Integer.MAX_VALUE, 49, 38, 65, 97, 76, 13, 27, 49};
        System.out.printf("Before: %s\n", Arrays.toString(array));
        Heap h = new Heap(new BigRootHeapComparator());
        h.heapSort(array, 0, array.length - 1);
        System.out.printf("After : %s\n", Arrays.toString(array));
    }
}