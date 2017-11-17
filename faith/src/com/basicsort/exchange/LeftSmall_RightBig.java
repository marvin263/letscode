package com.basicsort.exchange;

public class LeftSmall_RightBig {
	public static int partition(int low, int high, int[] array) {
		int firstValue = array[low];

		while (low < high) {
            while (array[high] >= firstValue && high > low) {
				high--;
			}
			array[low] = array[high];

			while (array[low] <= firstValue && high > low) {
				low++;
			}
			array[high] = array[low];
		}
		array[low] = firstValue;
		System.out.printf("finally, low=%d, high=%d", low, high);
		return low;
	}

	public static void main(String[] args) {
		int[] array = { 50, 50, 90, 50, 50, 40, 80, 60, 20, 50 };
		partition(0, array.length - 1, array);
	}
}
