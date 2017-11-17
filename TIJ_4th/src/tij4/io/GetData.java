package tij4.io;

//: io/GetData.java
// Getting different representations from a ByteBuffer
import static net.mindview.util.Print.print;
import static net.mindview.util.Print.printnb;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.ShortBuffer;

public class GetData {
	private static final int BSIZE = 1024;

	public static void main(String[] args) {
		ByteBuffer bb = ByteBuffer.allocate(BSIZE);
		// Allocation automatically zeroes the ByteBuffer:
		int i = 0;
		while (i++ < bb.limit()) {
			if (bb.get() != 0) {
				print("nonzero");
			}
		}
		print("i = " + i);
		bb.rewind();// bb's position is 0
		// Store and read a char array:
		CharBuffer charBuffer = bb.asCharBuffer().put("Howdy!");// charBuffer's
																// position is
																// "Howdy!".length()
		// bb is sharing the array with charBuffer
		// charBuffer has put "Howdy", bb of course can read it
		char c;
		while ((c = bb.getChar()) != 0) {//bb's position is 0, charBuffer's position is 6
			printnb(c + " ");
		}
		print();
		// use charBuffer to display it
		displayCharBuffer(charBuffer);
		bb.rewind();
		// Store and read a short:
		ShortBuffer shortBuffer = bb.asShortBuffer().put((short) 471142);// shortBuffer's
																			// position
																			// is
																			// 1

		// bb is sharing the array with shortBuffer
		// shortBuffer has put (short) 471142
		// charBuffer is also sharing the array
		// use charBuffer to display it
		displayCharBuffer(charBuffer);
		print(bb.getShort());
		bb.rewind();
		// Store and read an int:
		bb.asIntBuffer().put(99471142);
		print(bb.getInt());
		bb.rewind();
		// Store and read a long:
		bb.asLongBuffer().put(99471142);
		print(bb.getLong());
		bb.rewind();
		// Store and read a float:
		bb.asFloatBuffer().put(99471142);
		print(bb.getFloat());
		bb.rewind();
		// Store and read a double:
		bb.asDoubleBuffer().put(99471142);
		print(bb.getDouble());
		bb.rewind();
		charBuffer.rewind();
		while ((c = charBuffer.get()) != 0) {
			printnb(c + " ");
		}
		print();
		System.out.println(CharBuffer.allocate(9).hasArray());
		;
		bb.hasArray();
	}

	private static void displayCharBuffer(CharBuffer charBuffer) {
		charBuffer.rewind();
		char c;
		while ((c = charBuffer.get()) != 0) {
			printnb(c + " ");
		}
		print();
	}
} /*
 * Output: i = 1025 H o w d y ! 12390 99471142 99471142 9.9471144E7 9.9471142E7
 */// :~
