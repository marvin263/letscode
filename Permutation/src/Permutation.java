import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permutation {

	public static void generatePermutation(byte[] orgnArray) {
		// byte[] newArray = reverseArray(orgnArray);
		byte[] newArray = orgnArray;
		int length = newArray.length;

		List<byte[]> currentContainer = new ArrayList<byte[]>();
		byte[] aArray = new byte[length];
		aArray[0] = newArray[0];
		currentContainer.add(aArray);

		List<byte[]> newSpawnedContainer = new ArrayList<byte[]>();

		for (int i = 1; i < newArray.length; i++) {
			for (byte[] oneAlignedArray : currentContainer) {
				AlignmentInfo aInfo = new AlignmentInfo(oneAlignedArray, i);
				List<byte[]> aContainer = aInfo.spawnAlignment(newArray[i]);
				newSpawnedContainer.addAll(aContainer);
			}
			currentContainer.clear();
			currentContainer.addAll(newSpawnedContainer);
			newSpawnedContainer.clear();
		}

		System.out.println("result count:" + currentContainer.size());
		for (byte[] bArray : currentContainer) {
			System.out.println(Arrays.toString(bArray));
		}
	}

	private static byte[] reverseArray(byte[] orgn) {
		int length = orgn.length;
		byte[] newArray = new byte[orgn.length];
		for (int i = length - 1; i >= 0; i--) {
			newArray[length - i - 1] = orgn[i];
		}
		return newArray;
	}

	public static void main(String[] args) {
		generatePermutation(new byte[] { 1, 2, 3, 4 });
	}

	/** static helper class */
	static class AlignmentInfo {
		private byte[] orgnArray;
		private int usedCount;

		private AlignmentInfo(byte[] orgnArray, int usedCount) {
			this.orgnArray = orgnArray;
			this.usedCount = usedCount;
		}

		private List<byte[]> spawnAlignment(byte newElement) {
			System.out.println("newElement=" + newElement + ", orgnArray="
					+ Arrays.toString(orgnArray));
			int usedCount = this.usedCount;
			int orgnLength = orgnArray.length;

			List<byte[]> listContainer = new ArrayList<byte[]>(usedCount + 1);
			// put newElement at index i
			for (int i = 0; i < usedCount; i++) {
				AlignmentInfo aInfo = new AlignmentInfo(new byte[orgnLength], 0);
				listContainer.add(aInfo.orgnArray);

				// elements before i
				System.arraycopy(orgnArray, 0, aInfo.orgnArray, 0, i);
				// elements at i
				aInfo.orgnArray[i] = newElement;
				// elements after i
				System.arraycopy(orgnArray, i, aInfo.orgnArray, i + 1,
						usedCount - i);
				aInfo.usedCount++;
			}

			listContainer.add(orgnArray);
			orgnArray[usedCount] = newElement;
			usedCount++;

			for (byte[] aArray : listContainer) {
				System.out.println(Arrays.toString(aArray));
			}
			return listContainer;
		}

		public static void main(String[] args) {
			byte[] aaa = new byte[] { 1, 2, 3, 0, 0 };
			AlignmentInfo aInfo = new AlignmentInfo(aaa, 3);
			aInfo.spawnAlignment((byte) 4);
		}
	}
}
