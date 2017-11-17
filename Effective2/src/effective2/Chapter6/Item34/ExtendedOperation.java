package effective2.Chapter6.Item34;

// Emulated extension enum - Page 166-167

import java.util.*;

public enum ExtendedOperation implements Operation {
	EXP("^") {
		public double apply(double x, double y) {
			return Math.pow(x, y);
		}
	},
	REMAINDER("%") {
		public double apply(double x, double y) {
			return x % y;
		}
	};

	private final String symbol;

	ExtendedOperation(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	// Test class to exercise all operations in "extension enum" - Page 167
	public static void main(String[] args) {
		double x = 0.2;// Double.parseDouble(args[0]);
		double y = 1.45;// Double.parseDouble(args[1]);
		test(ExtendedOperation.class, x, y);

		System.out.println(); // Print a blank line between tests
		test2(Arrays.asList(ExtendedOperation.values()), x, y);
	}

	// test parameter is a bounded type token (Item 29)
	// Marvin: 就是 String.class 这个样子
	private static <T extends Enum<T> & Operation> void test(Class<T> opSet,
			double x, double y) {
		// Marvin: 根据 Class 得到 其所有的 enum instance
		for (Operation op : opSet.getEnumConstants())
			System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
	}

	// test parameter is a bounded wildcard type (Item 28)
	// Marvin: 这个好理解，就是一般的 unbounded wildcard type
	private static void test2(Collection<? extends Operation> opSet, double x,
			double y) {
		for (Operation op : opSet)
			System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
	}
}
