package tij4.io;

//: io/MyWorld.java
import static net.mindview.util.Print.print;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class House implements Serializable {
}

class Animal implements Serializable {
	private String name;
	private House preferredHouse;

	Animal(String nm, House h) {
		name = nm;
		preferredHouse = h;
	}

	public String toString() {
		return name + "[" + super.toString() + "], " + preferredHouse + "\n";
	}
}

public class MyWorld {
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		House house = new House();
		List<Animal> animals = new ArrayList<Animal>();
		animals.add(new Animal("Bosco the dog", house));
		animals.add(new Animal("Ralph the hamster", house));
		animals.add(new Animal("Molly the cat", house));
		print("Original animals: " + animals);
		// Marvin:
		// 1. 可以通过一个 字节数组 来使用对象序列化
		// 2. Serializable 实现的是“深度复制 deep copy”。复制了 整个对象网（而不仅仅是基本对象及其引用）
		ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
		ObjectOutputStream o1 = new ObjectOutputStream(buf1);
		// Marvin:
		// 1. 这里 向同一个流 写入了 2 次，所以，后面将其恢复时（animals1,
		// animals2），animals1和animals2是同一个对象
		// 而且，其内包含的 house 也是同一个对象
		o1.writeObject(animals);
		o1.writeObject(animals); // Write a 2nd set

		// Write to a different stream:
		ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
		ObjectOutputStream o2 = new ObjectOutputStream(buf2);
		// Marvin
		// 2. 这里 向另外一个流 写入。后面恢复时（animals3），Java 无法知道 其他流（前面那个流，即：buf1）中的对象和此流
		// 内的对象是同一个对象（同一个流内，Java可以知道“吼吼，它们指的是同一个对象”），所以，此流恢复时，
		// 再产生了一次“和前面完全不同的又一个 对象网”
		o2.writeObject(animals);

		// Now get them back:
		ObjectInputStream in1 = new ObjectInputStream(new ByteArrayInputStream(
				buf1.toByteArray()));
		ObjectInputStream in2 = new ObjectInputStream(new ByteArrayInputStream(
				buf2.toByteArray()));

		@SuppressWarnings("unchecked")
		List<Animal> animals1 = (List<Animal>) in1.readObject(), animals2 = (List<Animal>) in1
				.readObject(), animals3 = (List<Animal>) in2.readObject();
		//Marvin: animals1, animals2 是同一个 stream 恢复过来的。它们指向同一个对象
		print("animals1: " + animals1);
		print("animals2: " + animals2);
		//Marvin: 另一个stream，所以，这和上面是完全不同了 又一个 对象（指向的house当然也是不同的）
		print("animals3: " + animals3);
	}
}
