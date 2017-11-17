//: generics/coffee/CoffeeGenerator.java
// Generate different types of Coffee:
package tij4.generics.coffee;

import java.util.Iterator;
import java.util.Random;

import net.mindview.util.Generator;

public class CoffeeGenerator implements Generator<Coffee>, Iterable<Coffee> {
	private Class[] types = { Latte.class, Mocha.class, Cappuccino.class,
			Americano.class, Breve.class, };
	private static Random rand = new Random(47);

	public CoffeeGenerator() {
	}

	// For iteration:
	private int size = 0;

	public CoffeeGenerator(int sz) {
		size = sz;
	}

	public Coffee next() {
		try {
			// Marvin: 随机给出一种 Coffee
			return (Coffee) types[rand.nextInt(types.length)].newInstance();
			// Report programmer errors at run time:
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Marvin: 这是 nonstatic member class
	class CoffeeIterator implements Iterator<Coffee> {
		int count = size;

		public boolean hasNext() {
			return count > 0;
		}

		public Coffee next() {
			count--;
			// Marvin: 这就是用 nonstatic member class 的好处
			// 直接访问 enclosing class 内的所有 static/instance field/method
			return CoffeeGenerator.this.next();
		}

		public void remove() { // Not implemented
			throw new UnsupportedOperationException();
		}
	}

	// Marvin: java.lang.Iterable 要求必须要返回一个
	// java.util.Iterator（java.lang.Iterable使得可以直接用 foreach ）
	public Iterator<Coffee> iterator() {
		return new CoffeeIterator();
	}

	public static void main(String[] args) {
		CoffeeGenerator gen = new CoffeeGenerator();
		for (int i = 0; i < 5; i++)
			System.out.println(gen.next());
		for (Coffee c : new CoffeeGenerator(5))
			System.out.println(c);
	}
} /*
 * Output: Americano 0 Latte 1 Americano 2 Mocha 3 Mocha 4 Breve 5 Americano 6
 * Latte 7 Cappuccino 8 Cappuccino 9
 */// :~
