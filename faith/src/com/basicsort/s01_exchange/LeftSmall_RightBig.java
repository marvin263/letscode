package com.basicsort.s01_exchange;

public class LeftSmall_RightBig {
    // high同样是 inclusive 哦
    public static int partition(int low, int high, int[] array) {
        int firstValue = array[low];

        while (low < high) {
            while (low < high && array[high] >= firstValue) {
                high--;
            }
            // 高索引的值 是要大的，但，不满足了，整前面去
            array[low] = array[high];

            while (low < high && array[low] <= firstValue) {
                low++;
            }
            // 低索引的值 是要小的，但，不满足了，整后面去
            array[high] = array[low];
        }

        array[low] = firstValue;

        System.out.printf("finally, low=%d, high=%d", low, high);
        return low;
    }

    public static void main(String[] args) {
        int[] array = {50, 50, 90, 50, 50, 40, 80, 60, 20, 50};
        partition(0, array.length - 1, array);
    }
}
