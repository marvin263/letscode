package com.interview;

import java.util.Arrays;

public class TopN {
    // Find the top n number from more than 100,000,000 numbers
    public static int[] topN(int n, IntGenerator gi) {
        // please note: 0 element is ignored
        int[] arrayTopN = new int[n];
        for (int i = 0; i < arrayTopN.length; i++) {
            arrayTopN[i] = gi.next();
        }

        Heap h = new Heap(new Heap.SmallRootHeapComparator());
        h.buildHeap(arrayTopN, 0, arrayTopN.length - 1);

        while (gi.hasNext()) {
            insertNewNumber(arrayTopN, gi.next(), h);
        }
        return arrayTopN;
    }

    private static void insertNewNumber(int[] arrayTopN, int newNumber, Heap h) {
        // too small, just ignore the newNumber
        if (arrayTopN[0] >= newNumber) {
            return;
        }
        // newNumber > smallRootHeap[1]
        arrayTopN[0] = newNumber;
        h.siftDown(arrayTopN, 0, arrayTopN.length - 1, 0);
    }

    public static void test_TopN(int n) throws Exception {
        long begin = System.currentTimeMillis();
        IntGenerator gi = new IntGenerator(1000);
        int[] arrayTopN = topN(n, gi);

        System.out.println(Arrays.toString(arrayTopN));

        System.out.printf("TopN cost ms: %d\n",
                (System.currentTimeMillis() - begin));

        System.out.println(gi.toString());

    }

    public static void main(String[] args) throws Exception {
        test_TopN(10);
    }
}


