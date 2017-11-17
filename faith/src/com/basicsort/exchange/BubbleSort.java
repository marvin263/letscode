package com.basicsort.exchange;

import com.basicsort.AbstractBasicSort;

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
public class BubbleSort extends AbstractBasicSort {

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
		runTest(new BubbleSort());
	}
}
