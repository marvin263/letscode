package tij4.generics;

//: generics/ArrayOfGeneric.java

public class ArrayOfGeneric {
	static final int SIZE = 100;
	static Generic<Integer>[] gia;

	public static void main(String[] args) {
		// 1. Compiles; but produces ClassCastException when in Runtime:
		// ! gia = (Generic<Integer>[])new Object[SIZE];

		// 2. Compile Error: Cannot create a generic array of Generic<Integer>
		// array DOSE NOT work
		// new ArrayList<String> dose work
		// ! gia = (Generic<Integer>[]) new Generic<Integer>[SIZE];

		// Runtime type is the raw (erased) type, that is "type is Generic"
		gia = (Generic<Integer>[]) new Generic[SIZE];
		System.out.println(gia.getClass().getSimpleName());// Generic[]

		gia[0] = new Generic<Integer>();
		gia[1] = new Object(); // Compile-time error:
								// "cannot convert from Object to Generic<Integer>"

		gia[2] = new Generic<Double>();// Compile-time error:
										// "cannot convert from Generic<Double> to Generic<Integer>"
	}
}
