package tij4.generics;

//: generics/Holder.java

public class Holder<T> {
	private T value;

	public Holder() {
	}

	public Holder(T val) {
		value = val;
	}

	public void set(T val) {
		value = val;
	}

	public T get() {
		return value;
	}

	public boolean equals(Object obj) {
		return value.equals(obj);
	}

	public static void main(String[] args) {
		Holder<Apple> Apple = new Holder<Apple>(new Apple());
		Apple d = Apple.get();
		Apple.set(d);
		// 1. Type mismatch: cannot convert from Holder<Apple> to Holder<Fruit>
		Holder<Fruit> fruitCannotUpcast = Apple; // Cannot upcast
		// 2. 加了 wildcard 后就可以 upcast 啦
		Holder<? extends Object> fruitObject = Apple; // OK
		// 2. 加了 wildcard 后就可以 upcast 啦
		Holder<? extends Fruit> fruit = Apple; // OK

		Fruit p = fruit.get();
		d = (Apple) fruit.get(); // Returns 'Object'
		try {
			Orange c = (Orange) fruit.get(); // No warning
		} catch (Exception e) {
			System.out.println(e);
		}
		// 3. set() argument is also "? extends Fruit", which means it needs a
		// SPECIFIC type of Fruit
		// but the compiler does not know whether the type of your argument is
		// just the "SPECIFIC" type
		fruit.set(new Fruit()); // Cannot call set()
		fruit.set(new Apple()); // Cannot call set()
		fruit.set(new Object()); // Cannot call set()
		System.out.println(fruit.equals(d)); // OK
	}
} /*
 * Output: (Sample) java.lang.ClassCastException: Apple cannot be cast to Orange
 * true
 */// :~
