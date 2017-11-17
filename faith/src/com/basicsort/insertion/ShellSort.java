package com.basicsort.insertion;

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
public class ShellSort extends AbstractBasicSort {

	@Override
	public int[] doSort(int start, int end, int[] array) {
		straighInsertSort(start, end, array);
		return array;
	}

	private void straighInsertSort(int start, int end, int[] array) {
		for (int i = start + 1; i <= end; i++) {
			int curValue = array[i];
			int newIndex = start;
			for (int s = i - 1; s >= start; s--) {
				if (curValue < array[s]) {
					array[s + 1] = array[s];// (s+1) shifts back
				} else {
					newIndex = s + 1;// curValue should fall into (s+1)
					break;
				}
			}
			array[newIndex] = curValue;
		}
	}

	public static void main(String[] args) {
		int[] array = new int[] { 35, 2, 47, 10, 4, 23, 10, 12, 1, 32 };
		runTest(new ShellSort());
	}
}
