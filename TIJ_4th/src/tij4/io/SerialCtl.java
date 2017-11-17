package tij4.io;

//: io/SerialCtl.java
// Controlling serialization by adding your own
// writeObject() and readObject() methods.
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Marvin: 1. non-static and non-transient fields are handled by:
 * ObjectOutputStream.defaultRead(Write)Object();
 * 
 * 2. use private read(write)Object to explicitly save and restore transient
 * fields
 * 
 * 3. NO CONSTRUCTOR WILL BE INVOKED(only relays on binary stream)
 * 
 * @author MarvinLi
 * 
 */
public class SerialCtl implements Serializable {
	private String a;
	private transient String b;

	public SerialCtl() {
		System.out.println("Default Constructor");
	}

	public SerialCtl(String aa, String bb) {
		a = "Not Transient: " + aa;
		b = "Transient: " + bb;
		System.out.println("Two arguments constructor");
	}

	public String toString() {
		return a + "\n" + b;
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		// Marvin: non-static and non-transient field are handled by this
		stream.defaultWriteObject();
		// You must handle transient fields yourself, if you want to save and
		// restore them
		stream.writeObject(b);
	}

	private void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		stream.defaultReadObject();
		b = (String) stream.readObject();
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		SerialCtl sc = new SerialCtl("Test1", "Test2");
		System.out.println("Before:\n" + sc);

		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(buf);
		// Marvin: pay attention here, just write an object (sc) into
		// ByteArrayOutputStream
		o.writeObject(sc);
		// Now get it back:
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				buf.toByteArray()));
		// Marvin: NO CONSTRUCTOR WILL BE INVOKED
		SerialCtl sc2 = (SerialCtl) in.readObject();

		System.out.println("After:\n" + sc2);
	}
} /*
 * Output: Before: Not Transient: Test1 Transient: Test2 After: Not Transient:
 * Test1 Transient: Test2
 */// :~
