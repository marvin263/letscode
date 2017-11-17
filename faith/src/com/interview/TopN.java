package com.interview;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import com.util.CommUtil;

public class TopN {
	// Find the top n number from more than 100,000,000 numbers
	public static int[] topN(int n, GenerateInt gi) {
		// please note: 0 element is ignored
		int[] arrayTopN = new int[n + 1];
		arrayTopN[0] = Integer.MAX_VALUE;
		int i = 1;
		while (gi.hasNext() && (i <= arrayTopN.length - 1)) {
			arrayTopN[i] = gi.next();
			i++;
		}
		Heap.constructSmallRootHeap(arrayTopN);

		while (gi.hasNext()) {
			insertNewNumber(gi.next(), arrayTopN);
		}
		return arrayTopN;
	}

	private static void insertNewNumber(int newNumber, int[] smallRootHeap) {
		// too small, just ignore the newNumber
		if (smallRootHeap[1] >= newNumber) {
			return;
		}
		// newNumber > smallRootHeap[1]
		smallRootHeap[1] = newNumber;
		Heap.sinkRoot(1, smallRootHeap);
	}

	public static void test_TopN(int n, String fullPath) throws Exception {
		long begin = System.currentTimeMillis();

		FileInputStream fis = new FileInputStream(fullPath);
		int[] arrayTopN = topN(n, new GenerateInt(fis));
		fis.close();
		System.out.println(Arrays.toString(arrayTopN));

		System.out.printf("TopN cost millionseconds: %d",
				(System.currentTimeMillis() - begin));
	}

	public static void main(String[] args) throws Exception {
		String fullPath = "/home/marvin/random.dat";
		test_TopN(50, fullPath);
	}

}

class Heap {
	// Please note: 0 element is ignored
	public static void constructSmallRootHeap(int[] orgnArray) {
		// 0 element is not included
		int validElementCount = orgnArray.length - 1;
		int lastNonleaf = validElementCount / 2;

		int curIndex = lastNonleaf;
		while (curIndex >= 1) {
			sinkRoot(curIndex, orgnArray);
			curIndex--;
		}
	}

	public static void sinkRoot(int rootIndex, int[] smallRootHeap) {
		int lastIndex = smallRootHeap.length - 1;
		int curIndex = rootIndex;
		int left = 2 * curIndex;
		int right = left + 1;
		while (left <= lastIndex) {
			int minValueIndex = left;
			// right child exists
			if (right <= lastIndex) {
				minValueIndex = (smallRootHeap[left] > smallRootHeap[right]) ? right
						: left;
			}
			// no right child at all
			else {
				minValueIndex = left;
			}

			// parent is bigger. should down
			if (smallRootHeap[curIndex] > smallRootHeap[minValueIndex]) {
				CommUtil.swap(curIndex, minValueIndex, smallRootHeap);

				curIndex = minValueIndex;
				left = 2 * curIndex;
				right = left + 1;
			}
			// parent is smaller. good, we've done
			else {
				break;
			}
		}
	}

	public static void main(String[] args) {
		int[] orgnArray = { Integer.MAX_VALUE, 49, 38, 65, 97, 76, 13, 27, 49 };
		System.out.printf("Before: %s\n", Arrays.toString(orgnArray));
		constructSmallRootHeap(orgnArray);
		System.out.printf("After : %s\n", Arrays.toString(orgnArray));
	}
}

class GenerateInt {
	// To generate a random file, you can use the following linux command
	// dd if=/dev/urandom of=random.dat bs=1M count=512
	private DataInputStream dis;
	private int i;
	private long totalCount = 0;

	public GenerateInt(FileInputStream fis) {
		try {
			this.dis = new DataInputStream(new BufferedInputStream(fis));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasNext() {
		try {
			i = dis.readInt();
			totalCount++;
		} catch (Exception e) {
			System.out.println("Generate int count: " + totalCount);
			return false;
		}
		return true;
	}

	public int next() {
		return i;
	}

	private static void readIntFromFile(String fullPath) throws Exception {
		FileInputStream fis = new FileInputStream(fullPath);
		long begin = System.currentTimeMillis();
		GenerateInt gi = new GenerateInt(fis);
		while (gi.hasNext()) {
			// System.out.println(gi.next());
		}
		System.out.printf("Read int cost: %d",
				(System.currentTimeMillis() - begin));
		fis.close();
	}

	public static void main(String[] args) throws Exception {
		String randomFile = "/home/marvin/random.dat";
		readIntFromFile(randomFile);
	}
}
