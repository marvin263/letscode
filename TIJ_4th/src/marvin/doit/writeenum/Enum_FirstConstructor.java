package marvin.doit.writeenum;

public enum Enum_FirstConstructor {
	ONE, TWO, Three;
	private Enum_FirstConstructor() {
		System.out.println("In constructor");
	}

	static {
		System.out.println("Static block");
	}

	public static void main(String[] args) {
		System.out.println(Enum_FirstConstructor.ONE);
	}
}
