package tij4.io;

//: io/IntBufferDemo.java
// Manipulating ints in a ByteBuffer with an IntBuffer
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IntBufferDemo {
	private static final int BSIZE = 1024;

	public static void main(String[] args) {
		ByteBuffer bb = ByteBuffer.allocate(BSIZE);
		IntBuffer ib = bb.asIntBuffer();
		// Marvin: test hasArray()
		System.out.println("bb.hasArray(): " + bb.hasArray());// true
		System.out.println("ib.hasArray(): " + ib.hasArray());// false

		// Store an array of int:
		ib.put(new int[] { 11, 42, 47, 99, 143, 811, 1016 });
		// Absolute location read and write:
		System.out.println(ib.get(3));// 99
		ib.put(3, 1811);// 99 is replaced with 1811

		System.out.println("before flip limit: " + ib.limit());// 256
		System.out.println("before flip capacity: " + ib.capacity());// 256
		// Setting a new limit before rewinding the buffer.
		ib.flip();
		System.out.println("after flip limit: " + ib.limit());// 7
		System.out.println("after flip capacity: " + ib.capacity());// 256
		while (ib.hasRemaining()) {
			int i = ib.get();
			System.out.print(i + ", ");
		}
	}
} /*
 * Output: 11, 42, 47, 1811, 143, 811, 1016,
 */// :~
