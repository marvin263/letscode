package tij4.generics;

//: generics/Erased.java
// {CompileTimeError} (Won't compile)

public class Erased<T> {
	private final static int SIZE = 100;

	public void f(Object arg) {
		if (arg instanceof T) {
		} // Error
		T var = new T(); // Error
		T[] array1 = new T[SIZE]; // Error
		// Compile Warning: "Type safety: Unchecked cast from Object[] to T[]"
		T[] array2 = (T[]) new Object[SIZE]; // Unchecked warning
	}
} // /:~
