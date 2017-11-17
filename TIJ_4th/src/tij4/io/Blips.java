package tij4.io;

//: io/Blips.java
// Simple use of Externalizable & a pitfall.
import static net.mindview.util.Print.print;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Marvin: WHEN RESTORE
 * 
 * 1. Serializable, ONLY relay on the binary files
 * <p>
 * 2. Externalizable
 * 
 * a. invoke default constructor(explicit public xxx() must exist, otherwise an
 * error will be thrown)
 * 
 * b. invoke readExternal
 * 
 * @author MarvinLi
 * 
 */
class Blip1 implements Externalizable {
	public Blip1() {
		print("Blip1 Constructor");
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		print("Blip1.writeExternal");
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		print("Blip1.readExternal");
	}
}

class Blip2 implements Externalizable {
	// Marvin: It's the very NON-PUBLIC constructor that make recovery be ERROR
	Blip2() {
		print("Blip2 Constructor");
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		print("Blip2.writeExternal");
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		print("Blip2.readExternal");
	}
}

public class Blips {
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		print("Constructing objects:");
		Blip1 b1 = new Blip1();
		Blip2 b2 = new Blip2();
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(
				"Blips.out"));
		print("Saving objects:");
		// Marvin：同一个文件中，写入了 2 个Object
		o.writeObject(b1);
		o.writeObject(b2);
		o.close();
		// Now get them back:
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"Blips.out"));
		print("Recovering b1:");
		b1 = (Blip1) in.readObject();
		// OOPS! Throws an exception:
		print("Recovering b2:");
		b2 = (Blip2) in.readObject();
	}
}
