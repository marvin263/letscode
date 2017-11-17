package marvin.doit.writeenum;

import java.util.Arrays;
import java.util.Collection;

public enum ExtendedOperation implements Operation {
	EXP("^") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	},
	REMAINDER("%") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	};
	private final String symbol;

	private ExtendedOperation(String symbol) {
		this.symbol = symbol;
	}

	public static void main(String[] args) {
		double x = 342.53d;
		double y = 852.53d;
		test1(ExtendedOperation.class, x, y);
		test2(Arrays.asList(ExtendedOperation.values()), x, y);
	}

	private static <T extends Enum<T> & Operation> void test1(Class<T> opSet, double x,
			double y) {
		for (Operation op : opSet.getEnumConstants()) {
			System.out.println(op.apply(x, y));
		}
	}

	private static void test2(Collection<? extends Operation> opSet, double x,
			double y) {
		for (Operation op : opSet) {
			System.out.println(op.apply(x, y));
		}
	}
}
