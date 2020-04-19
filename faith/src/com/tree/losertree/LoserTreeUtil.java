package com.tree.losertree;

import java.util.Arrays;

import com.util.CommUtil;

public class LoserTreeUtil {
    private static final int INDEX_OF_MINIMUM_VALUE = -1;

    public static LSNode createVisibleLoserTree(int[] ls) {
        if (ls == null || ls.length == 0) {
            return null;
        }
        LSNode[] arrayNode = new LSNode[ls.length];
        for (int i = 1; i < ls.length; i++) {
            arrayNode[i] = LSNode.createNode(i, ls[i]);
        }

        int leftHalf = arrayNode.length / 2;
        for (int i = 1; i < leftHalf; i++) {
            LSNode parent = arrayNode[i];

            int left = i * 2;
            parent.L = arrayNode[left];

            int right = left + 1;
            if (right <= arrayNode.length - 1) {
                parent.R = arrayNode[right];
            }
        }
        return arrayNode[1];
    }

    static class ValueContainer {
        private int valueIndex;
        private boolean max;

        public static ValueContainer createVC(int valueIndex) {
            ValueContainer vc = new ValueContainer();
            vc.setValueIndex(valueIndex);
            return vc;
        }

        public boolean max() {
            return max;
        }

        public int valueIndex() {
            return valueIndex;
        }

        public void setValueIndex(int orgnIndex) {
            max = false;
            this.valueIndex = orgnIndex;
        }

        public void setMax() {
            max = true;
            valueIndex = -1;
        }
    }

    public static void main(String[] args) {
        test_createLoserTree();
    }

    private static void fdf(int[][] twoDArray) {
        int k = twoDArray.length;

        int[] currentIndex = new int[k];

        int[] arrayToBeSearch = new int[k];
        for (int i = 0; i < arrayToBeSearch.length; i++) {
            arrayToBeSearch[i] = twoDArray[i][0];
            currentIndex[i] = 0;
        }

        int[] loserTree = createLoserTree(arrayToBeSearch);
        while (arrayToBeSearch[loserTree[0]] != Integer.MAX_VALUE) {
            int q = loserTree[0];
            System.out.println(q + ", ");

        }

        for (int i = 0; i < k - 1; i++) {
            int[] oneArray = twoDArray[i];

        }

    }

    public static int findMin(int[] array) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int smallestIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[smallestIndex] > array[i]) {
                smallestIndex = i;
            }
        }
        return smallestIndex;
    }

    public static void test_createLoserTree() {
        int[] array = CommUtil.genereateRandomArray(20, 100);
        array = new int[] { 36, 42, 47, 72, 4, 18, 80, 7, 79, 67, 70, 21, 13,
                37, 43, 50, 57, 21, 39, 66 };
        createLoserTree(array);
    }

    public static int[] createLoserTree(int[] values) {
        System.out.println("Original values:" + Arrays.toString(values));
        int smallestIndex = findMin(values);
        System.out.println("values' smallest index: " + smallestIndex);

        ValueContainer[] containers = new ValueContainer[values.length];
        for (int i = 0; i < containers.length; i++) {
            containers[i] = ValueContainer.createVC(i);
        }

        int[] ls = new int[values.length];
        for (int i = 0; i < ls.length; i++) {
            ls[i] = INDEX_OF_MINIMUM_VALUE;
        }

//        for (int i = values.length - 1; i >= 0; i--) {
//            adjust(ls, containers, i, values);
//        }
        for (int i = 0; i < values.length; i++) {
            adjust(ls, containers, i, values);
        }

        System.out.println("Loser tree:" + Arrays.toString(ls));

        return ls;
    }

    /**
     * 0. 让小值，在牛逼的道路上 一路狂奔。 对于任意一个 leaf，沿着 leaf---->root 的路径，让小值 一路比下去
     * 
     * 1. <b>a node is always compared with its parent</b>
     * 
     * 2. Consume each node from this leaf up to root
     * 
     * 3. <code>ls</code>'s elements: index of <code>values</code>
     * 
     * 
     * 
     * @param ls
     * @param containers
     * @param valueIndex
     * @param values
     */
    private static void adjust(int[] ls, final ValueContainer[] containers,
            int valueIndex, int[] values) {
        // valueIndex == 2*treeIndex - k
        int treeIndex = (valueIndex + containers.length) >> 1;
        // Consume each node from this leaf up to root
        while (treeIndex > 0) {
            // it's already the minimum values
            // ls's word less than treeIndex would never change again.
            // we can return directly INDEX_OF_MINIMUM_VALUE now
            if (valueIndex == INDEX_OF_MINIMUM_VALUE) {
                break;
            }
            int parentIndex = ls[treeIndex];
            // me bigger
            if (parentIndex == INDEX_OF_MINIMUM_VALUE // parent is minimum
                                                        // values
                    || greatThan(containers[valueIndex],
                            containers[parentIndex], values)) {
                // 1. move up childIndex
                ls[treeIndex] = valueIndex;
                // 2. smaller word should continue to compare
                valueIndex = parentIndex;
            }
            treeIndex = treeIndex / 2;
        }
        ls[0] = valueIndex;
    }

    private static boolean greatThan(ValueContainer A, ValueContainer B,
            int[] values) {
        if (A.max() && B.max()) {
            return true;
        }

        if (A.max()) {
            return true;
        }
        if (B.max()) {
            return false;
        }

        // b is neither max nor min
        return values[A.valueIndex()] > values[B.valueIndex()];
    }

}
