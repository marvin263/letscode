public class Test {
	public static void main(String[] args) {
		System.out.println("Test: " + Test.class.getClassLoader());
		new TestLib().print();
	}
}
