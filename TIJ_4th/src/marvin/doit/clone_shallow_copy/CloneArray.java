package marvin.doit.clone_shallow_copy;

public class CloneArray implements Cloneable {
	public Object[] array;

	public CloneArray() {
		array = new Object[10];
	}

	@Override
	public CloneArray clone() throws CloneNotSupportedException {
		return (CloneArray) super.clone();
	}

	public String toString() {
		return array2String(array);
	}

	public static String array2String(Object[] array) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : array) {
			sb.append(obj + ", ");
		}
		return sb.toString();
	}

	/**
	 * Marvin: 有了外面的 wrapper，直接clone wrapper时，里面的数组是 <b>不被</b> 克隆的
	 * 
	 * 新对象 和 orgn <b>共用同一个</b> array的reference
	 * 
	 * @throws Exception
	 */
	private static void testWrapper() throws Exception {
		CloneArray orgn = new CloneArray();
		orgn.array[0] = "Hello 0";

		CloneArray copy = orgn.clone();
		copy.array[9] = "Hello 9";
		System.out.println(orgn);
		System.out.println(copy);
	}

	/**
	 * Marvin: 直接 array.clone()，这就不同了，这时：<b> new了一个新的 array出来 </b>
	 * 
	 * 当然，新 new 出的 array，里面保存的 reference 是“以前reference的copy”，然后 他们 指向的是 同一个对象
	 * 
	 * @throws Exception
	 */
	private static void testArraySelf() throws Exception {
		CloneArray only4Array = new CloneArray();

		Object[] array = only4Array.array;

		Object[] copyOfArray = array.clone();
		copyOfArray[0] = "Array 0";
		copyOfArray[9] = "Array 9";
		
		System.out.println(array2String(array));
		System.out.println(array2String(copyOfArray));
	}

	public static void main(String[] args) throws Exception {
		testWrapper();
		testArraySelf();
	}
}
