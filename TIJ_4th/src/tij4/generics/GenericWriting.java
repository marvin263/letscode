package tij4.generics;

//: generics/GenericWriting.java
import java.util.ArrayList;
import java.util.List;

public class GenericWriting {
	static <T> void writeExact(List<T> list, T item) {
		list.add(item);
	}

	static List<Apple> apples = new ArrayList<Apple>();
	static List<Fruit> fruit = new ArrayList<Fruit>();

	static void f1() {
		writeExact(apples, new Apple());
		//1. 无论怎么说，Marvin同学找到了 Bruce Eckel 的一个错误
		// Error: Incompatible types: found Fruit, required Apple
		writeExact(fruit, new Apple());
		// 这没啥特殊的啊，好得很
		fruit.add(new Apple());
	}

	static <T> void writeWithWildcard(List<? super T> list, T item) {
		list.add(item);
	}

	static void f2() {
		writeWithWildcard(apples, new Apple());
		writeWithWildcard(fruit, new Apple());
	}

	public static void main(String[] args) {
		f1();
		f2();
	}
} // /:~
