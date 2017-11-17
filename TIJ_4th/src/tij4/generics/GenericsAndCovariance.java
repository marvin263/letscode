package tij4.generics;

//: generics/GenericsAndCovariance.java
import java.util.ArrayList;

public class GenericsAndCovariance {
	public static void main(String[] args) {
		ArrayList<? extends Fruit> flist = new ArrayList<Apple>();
		// 1. Compile Error: can't add any type of object:
		flist.add(new Apple());
		flist.add(new Fruit());
		flist.add(new Object());
		flist.add(null); // Legal but uninteresting
		// 2. We know that it returns at least Fruit:
		Fruit f = flist.get(0);
		// 3. Wildcards allow covariance:
		ArrayList<? extends Fruit> flist2 = new ArrayList<Orange>();
		ArrayList<? extends Fruit> flist3 = new ArrayList<Jonathan>();
		flist2.addAll(flist3);
	}
} // /:~
