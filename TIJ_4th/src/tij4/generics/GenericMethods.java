package tij4.generics;

//: generics/GenericMethods.java

public class GenericMethods {
	public <T, U, Y, V, K, Z> void f(T x) {
		System.out.println(x.getClass().getName());
	}

	public static void main(String[] args) {
		GenericMethods gm = new GenericMethods();
		gm.f("");
		gm.f(1);
		gm.f(1.0);
		gm.f(1.0F);
		gm.f(1L);
		gm.f('c');
		gm.f(gm);
	}
}
