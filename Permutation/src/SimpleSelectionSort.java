import java.util.Arrays;

public class SimpleSelectionSort {
	/**
	 * Marvin: original array也被改变了
	 * 
	 * 
	 * 在这个基础上进行改进，就能得到 锦标赛排序
	 * n个记录中，找到最小值，需要进行 n-1 次比较
	 * 在剩下的 n-1 个中，找到最小值，并非一定进行 n-2 次比较
	 * 
	 * 我们要利用 前 n-1 次比较所得到的信息，从而减少后面各趟选择时的比较次数
	 * 
	 * @param array
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static int[] simpleSelectionSort(int[] array, int startIndex,
			int endIndex) {
		int[] result = new int[endIndex - startIndex + 1];

		int currentResultIndex = 0;
		for (int i = startIndex; i <= endIndex; i++) {
			for (int k = i + 1; k <= endIndex; k++) {
				if (array[i] > array[k]) {
					int tmp = array[i];
					array[i] = array[k];
					array[k] = tmp;
				}
			}
			result[currentResultIndex] = array[i];
			currentResultIndex++;

		}
		System.out.println(Arrays.toString(array));
		return result;
	}

	public static void main(String[] args) {
		int[] array = { 3, 5, 2, 9, 8, 0, 3, 5, 8, 4, 7 };
		int[] result = simpleSelectionSort(array, 3, array.length - 1);
		System.out.println(Arrays.toString(result));
	}
}
