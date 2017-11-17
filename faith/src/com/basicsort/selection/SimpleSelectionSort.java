package com.basicsort.selection;

import com.basicsort.AbstractBasicSort;
import com.util.CommUtil;

/**
 * <pre>
 * 稳定性：不稳定
 * 
 * 空间复杂度，辅助空间：O(1)
 * 
 * 时间复杂度
 *          最坏：O(n^2)
 * 
 *          最好：O(n^2)
 * 
 *          平均：O(n^2)
 * </pre>
 * 
 * @author marvin
 * 
 */
public class SimpleSelectionSort extends AbstractBasicSort {

	@Override
	public int[] doSort(int start, int end, int[] array) {
		simpleSelectionSort(start, end, array);
		return array;
	}

	/**
	 * 1. auxiliary space-------- Only a tmp int, used for swap
	 * 
	 * 
	 * 
	 * 选择排序(Selection sort)也是一种简单直观的排序算法。
	 * 
	 * 算法步骤：
	 * 
	 * 　　1)首先在未排序序列中找到 最小 元素，存放到排序序列的起始位置
	 * 
	 * 　　2)再从 剩余未排序元素中 继续寻找 最小 元素，然后放到 已排序 序列的末尾
	 * 
	 * 　　3)重复第二步，直到所有元素均排序完毕。
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
		runTest(new SimpleSelectionSort());
	}
}
