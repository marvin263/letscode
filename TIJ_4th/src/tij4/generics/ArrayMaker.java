package tij4.generics;

//: generics/ArrayMaker.java
import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayMaker<T> {
	private Class<T> kind;

	public ArrayMaker(Class<T> kind) {
		this.kind = kind;
	}

	// Marvin: You can see that "Generic will produce an warning"
	T[] create(int size) {
		return (T[]) Array.newInstance(kind, size);
	}

	// Marvin: "No warning in specific Type"
	String[] createSpecificClass(int size) {
		return (String[]) Array.newInstance(String.class, size);
	}

	public static void main(String[] args) {
		ArrayMaker<String> stringMaker = new ArrayMaker<String>(String.class);
		String[] stringArray = stringMaker.create(9);
		System.out.println(Arrays.toString(stringArray));
	}
} /*
 * Output: [null, null, null, null, null, null, null, null, null]
 */// :~
