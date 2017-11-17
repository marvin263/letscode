package marvin.sort;

public enum SortEnum {
	bubble {
		@Override
		public int[] sort(int left, int right, int[] array) {
			for (int k = left; k <= right; k++) {
				for (int in = k + 1; in <= right; in++) {
					if (array[k] > array[in]) {
						swap(array, k, in);
					}
				}
			}
			return array;
		}
	},
	quick {
		@Override
		public int[] sort(int left, int right, int[] array) {
			return array;
		}
	},
	heap {
		@Override
		public int[] sort(int left, int right, int[] array) {
			return array;
		}
	},
	insert {
		@Override
		public int[] sort(int left, int right, int[] array) {
			return array;
		}
	};

	public abstract int[] sort(int left, int right, int[] array);

	@Override
	public String toString() {
		return super.toString();
	}

	protected void swap(int[] array, int index1, int index2) {
		int temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
}
