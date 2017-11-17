package com.basicsort.other2;

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
public class MergeSort extends AbstractBasicSort {

	@Override
	public int[] doSort(int start, int end, int[] array) {
		return null;
	}

	public static void main(String[] args) {
		runTest(new MergeSort());
	}
}
