package marvin.sort;

import java.util.Arrays;
import java.util.Random;

public class Main {

	public void test() {
		Random r = new Random(47);
		int length = 12;
		int left = 4;
		int right = length - 3;

		int[] arrayToBeSorted = new int[length];
		for (int i = 0; i < length; i++) {
			arrayToBeSorted[i] = r.nextInt(100);
		}

		SortEnum[] allSE = SortEnum.values();
		for (SortEnum se : allSE) {
			testEachSort(se, left, right,
					Arrays.copyOf(arrayToBeSorted, arrayToBeSorted.length));
		}
	}

	private void testEachSort(SortEnum se, int left, int right, int[] array) {
		System.out.println(se);
		display(left, right, array);

		int[] sortedArray = se.sort(left, right, array);
		display(left, right, sortedArray);
		System.out.println();
	}

	private void display(int left, int right, int[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i == left) {
				sb.append("--- ");
			}
			sb.append(array[i]);
			if (i != array.length) {
				sb.append(", ");
			}
			if (i == right) {
				sb.append(" ---");
			}
		}
		System.out.println(sb.toString());
	}

	public static void main(String[] args) {
		new Main().test();
	}
}
