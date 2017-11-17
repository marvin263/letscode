package marvin.doit.writeenum;

public enum TestEnum {
	ONE, TWO, Three;
	TestEnum() {
		System.out.println("In constructor");
	}

	static {
		System.out.println("Static block");
	}

	public static void main(String[] args) {
		System.out.println(TestEnum.ONE);
	}
}
