package tij4.io;

//: io/Blip3.java
// Reconstructing an externalizable object.
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
 * 这个例子，彻底认清楚 Externalizable 怎么用的
 * 
 * Externalizable和Serializable 是一样的：
 *     a. Saving object     --> ObjectOutputStream.writeObject(externalizableObject)
 *     b. Recovering object --> ObjectInputStream.readObject(externalizableObject) 
 * 
 * Externalizable, when restore
 * 
 * a. invoke default constructor
 * 
 * b. invoke readExternal()
 * 
 * PLEASE NOTE: java.io.Externalizable stores NO FIELDS by default, if any field
 * needs to be stored, it must be explicitly stored in
 * {@link #writeExternal(ObjectOutput)}
 * 
 * @author MarvinLi
 * 
 */
public class Blip3 implements Externalizable {
	private int i=9;
	private String s="ddd"; // No initialization

	public Blip3() {
		print("Blip3 Constructor");
		// s, i not initialized
	}

	public Blip3(String x, int a) {
		print("Blip3(String x, int a)");
//		s = x;
//		i = a;
		// s & i initialized only in non-default constructor.
	}

	public String toString() {
		return s + i;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		print("Blip3.writeExternal");
		// You must do this:
		//out.writeObject(s);
		//out.writeInt(i);
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		print("Blip3.readExternal");
		// You must do this:
		//s = (String) in.readObject();
		//i = in.readInt();
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		print("Constructing objects:");
		Blip3 b3 = new Blip3("A String ", 47);
		print(b3);
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(
				"Blip3.out"));
		print("Saving object:");
		o.writeObject(b3);
		o.close();

		// Now get it back:
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"Blip3.out"));
		print("Recovering b3:");
		// Marvin: when restore, a. invoke default constructor b. invoke
		// readExternal
		b3 = (Blip3) in.readObject();
		print(b3);
	}
}

