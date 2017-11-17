package tij4.generics;

//: generics/GenericArray.java

public class GenericArray<T> {
	private T[] array;

	public GenericArray(int sz) {
		// Marvin: actual runtime type is Object[]
		array = (T[]) new Object[sz];
	}

	public void put(int index, T item) {
		array[index] = item;
	}

	public T get(int index) {
		return array[index];
	}

	// Method that exposes the underlying representation:
	public T[] rep() {
		return array;
	}

	public static void main(String[] args) {
		GenericArray<Integer> gai = new GenericArray<Integer>(10);
		// Compile OK.
		// but this causes a ClassCastException in Runtime
		// because the actual runtime type is Object[]
		Integer[] ia = gai.rep();
		// This is OK:
		Object[] oa = gai.rep();
	}
} // /:~
