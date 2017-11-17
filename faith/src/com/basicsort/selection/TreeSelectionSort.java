package com.basicsort.selection;

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
public class TreeSelectionSort extends AbstractBasicSort {

	@Override
	public int[] doSort(int start, int end, int[] array) {
		simpleSelectionSort(start, end, array);
		return array;
	}

	/**
	 * 1. auxiliary space-------- Only a tmp int, used for swap
	 * 
	 */
	public void simpleSelectionSort(int start, int end, int[] array) {
		for (int i = start; i <= end - 1; i++) {
			int minIndex = i;
			for (int k = i + 1; k <= end; k++) {
				if (array[k] < array[minIndex]) {
					minIndex = k;
				}
			}
			// Yes, find smaller element
			if (minIndex != i) {
				CommUtil.swap(i, minIndex, array);
			}
		}
	}

	public static void main(String[] args) {
		runTest(new TreeSelectionSort());
	}
}
