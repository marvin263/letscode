package marvin.doit.accept_both_then_lessSpecific;

public class A {
	public void method(Object obj) {
		System.out.println("Object");
	}

	public void method(String str) {
		System.out.println("String");
	}

	public static void main(String[] args) {
		A a = new A();
		a.method("123");
		a.method(new Object());
		a.method(null);
	}
}
