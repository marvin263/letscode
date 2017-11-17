import java.util.Arrays;

public class MergeSort {
	public static int[] merge(int[] sortedArray1, int startIndex1,
			int endIndex1, int[] sortedArray2, int startIndex2, int endIndex2) {

		int[] result = new int[(endIndex1 - startIndex1 + 1)
				+ (endIndex2 - startIndex2 + 1)];

		int curIndex1 = startIndex1;
		int curIndex2 = startIndex2;
		int currentResultIndex = 0;
		while (curIndex1 <= endIndex1 || curIndex2 <= endIndex2) {
			// array1 is still OK
			if (curIndex1 <= endIndex1) {
				// array2 still OK
				if (curIndex2 <= endIndex2) {
					if (sortedArray1[curIndex1] < sortedArray2[curIndex2]) {
						result[currentResultIndex] = sortedArray1[curIndex1];
						curIndex1++;
					} else {
						result[currentResultIndex] = sortedArray2[curIndex2];
						curIndex2++;
					}
				}
				// array2 done
				else {
					result[currentResultIndex] = sortedArray1[curIndex1];
					curIndex1++;
				}
			}

			// array1 done, only left array2
			else {
				result[currentResultIndex] = sortedArray2[curIndex2];
				curIndex2++;
			}

			currentResultIndex++;
		}
		return result;
	}

	public static void main(String[] args) {
		int[] array1 = { 1, 3, 5, 7, 9 };
		int[] array2 = { 0, 1, 2, 3, 4, 5, 6 };
		int[] rst = merge(array1, 2, array1.length - 1, array2, 4,
				array2.length - 1);
		System.out.println(Arrays.toString(rst));
	}
}
