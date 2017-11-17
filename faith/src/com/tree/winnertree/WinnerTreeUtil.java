package com.tree.winnertree;


public class WinnerTreeUtil {
	static class ValueContainer {
		private int valueIndex;
		private boolean max;

		public static ValueContainer createVC(int valueIndex) {
			ValueContainer vc = new ValueContainer();
			vc.setValueIndex(valueIndex);
			return vc;
		}

		public boolean max() {
			return max;
		}

		public int valueIndex() {
			return valueIndex;
		}

		public void setValueIndex(int orgnIndex) {
			max = false;
			this.valueIndex = orgnIndex;
		}

		public void setMax() {
			max = true;
			valueIndex = -1;
		}
	}

	public static void createWinnerTree(int[] values) {

	}

	public static void sortByWinnerTree(int[] wt) {

	}
}
