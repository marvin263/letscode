public class MarvinBit {
	/**
	 * All possible chars for representing a number as a String
	 */
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };

	/**
	 * Returns a string representation of the integer argument as an unsigned
	 * integer in base&nbsp;16.
	 * <p>
	 * The unsigned integer value is the argument plus 2<sup>32</sup> if the
	 * argument is negative; otherwise, it is equal to the argument. This value
	 * is converted to a string of ASCII digits in hexadecimal (base&nbsp;16)
	 * with no extra leading <code>0</code>s. If the unsigned magnitude is zero,
	 * it is represented by a single zero character <code>'0'</code> (
	 * <code>'&#92;u0030'</code>); otherwise, the first character of the
	 * representation of the unsigned magnitude will not be the zero character.
	 * The following characters are used as hexadecimal digits: <blockquote>
	 * 
	 * <pre>
	 * 0123456789abcdef
	 * </pre>
	 * 
	 * </blockquote> These are the characters <code>'&#92;u0030'</code> through
	 * <code>'&#92;u0039'</code> and <code>'&#92;u0061'</code> through
	 * <code>'&#92;u0066'</code>. If uppercase letters are desired, the
	 * {@link java.lang.String#toUpperCase()} method may be called on the
	 * result: <blockquote>
	 * 
	 * <pre>
	 * Integer.toHexString(n).toUpperCase()
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param i
	 *            an integer to be converted to a string.
	 * @return the string representation of the unsigned integer value
	 *         represented by the argument in hexadecimal (base&nbsp;16).
	 * @since JDK1.0.2
	 */
	public static String toHexString(int i) {
		return toUnsignedString(i, 4);
	}

	/**
	 * Returns a string representation of the integer argument as an unsigned
	 * integer in base&nbsp;8.
	 * <p>
	 * The unsigned integer value is the argument plus 2<sup>32</sup> if the
	 * argument is negative; otherwise, it is equal to the argument. This value
	 * is converted to a string of ASCII digits in octal (base&nbsp;8) with no
	 * extra leading <code>0</code>s.
	 * <p>
	 * If the unsigned magnitude is zero, it is represented by a single zero
	 * character <code>'0'</code> (<code>'&#92;u0030'</code>); otherwise, the
	 * first character of the representation of the unsigned magnitude will not
	 * be the zero character. The following characters are used as octal digits:
	 * <blockquote>
	 * 
	 * <pre>
	 * 01234567
	 * </pre>
	 * 
	 * </blockquote> These are the characters <code>'&#92;u0030'</code> through
	 * <code>'&#92;u0037'</code>.
	 * 
	 * @param i
	 *            an integer to be converted to a string.
	 * @return the string representation of the unsigned integer value
	 *         represented by the argument in octal (base&nbsp;8).
	 * @since JDK1.0.2
	 */
	public static String toOctalString(int i) {
		return toUnsignedString(i, 3);
	}

	/**
	 * Returns a string representation of the integer argument as an unsigned
	 * integer in base&nbsp;2.
	 * <p>
	 * The unsigned integer value is the argument plus 2<sup>32</sup> if the
	 * argument is negative; otherwise it is equal to the argument. This value
	 * is converted to a string of ASCII digits in binary (base&nbsp;2) with no
	 * extra leading <code>0</code>s. If the unsigned magnitude is zero, it is
	 * represented by a single zero character <code>'0'</code> (
	 * <code>'&#92;u0030'</code>); otherwise, the first character of the
	 * representation of the unsigned magnitude will not be the zero character.
	 * The characters <code>'0'</code> (<code>'&#92;u0030'</code>) and
	 * <code>'1'</code> (<code>'&#92;u0031'</code>) are used as binary digits.
	 * 
	 * @param i
	 *            an integer to be converted to a string.
	 * @return the string representation of the unsigned integer value
	 *         represented by the argument in binary (base&nbsp;2).
	 * @since JDK1.0.2
	 */
	public static String toBinaryString(int i) {
		return toUnsignedString(i, 1);
	}

	/**
	 * Convert the integer to an unsigned number.
	 */
	private static String toUnsignedString(int i, int shift) {
		char[] buf = new char[32];
		int charPos = 32;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			// System.out.println("i=" + i);
			buf[--charPos] = digits[i & mask];
			i >>>= shift;
		} while (i != 0);

		return new String(buf, charPos, (32 - charPos));
	}

	public static void main(String[] args) {
		int first = 32; // 0000 0000 0000 0000 0000 0000 0010 0000
		int second = -32;// 1111 1111 1111 1111 1111 1111 1110 0000

		System.out.println("Binary: " + toUnsignedString(first, 1));
		System.out.println("Octal: " + toUnsignedString(first, 3));
		System.out.println("Hex: " + toUnsignedString(first, 4));

		System.out.println("Binary: " + toUnsignedString(second, 1));
		System.out.println("Octal: " + toUnsignedString(second, 3));
		System.out.println("Hex: " + toUnsignedString(second, 4));

		// 3: 0000 0000 0000 0000 0000 0000 0000 0011
		// 3<<30: 1100 0000 0000 0000 0000 0000 0000 0000
		// 3<<31: 1000 0000 0000 0000 0000 0000 0000 0000
		// 3<<32: 0000 0000 0000 0000 0000 0000 0000 0011
		// 3<<33: 0000 0000 0000 0000 0000 0000 0000 0110
		// 3<<34: 0000 0000 0000 0000 0000 0000 0000 1100
		// 3<<35: 0000 0000 0000 0000 0000 0000 0001 1000
		System.out.println("Binary 31: " + toUnsignedString(3 << 31, 1));
		System.out.println("Binary 32: " + toUnsignedString(3 << 32, 1));
		System.out.println("Binary 33: " + toUnsignedString(3 << 33, 1));
		System.out.println("Binary 34: " + toUnsignedString(3 << 34, 1));
		System.out.println("Binary 35: " + toUnsignedString(3 << 35, 1));
		// 0
		System.out.println("(3<<31)<<1=" + ((3 << 31) << 1));
		// 0
		System.out.println("(3<<31)<<2=" + ((3 << 31) << 2));

		// 3: 0000 0000 0000 0000 0000 0000 0000 0011
		// 3>>1: 0000 0000 0000 0000 0000 0000 0000 0001
		// 3>>2: 0000 0000 0000 0000 0000 0000 0000 0000
		// 3>>3: 0000 0000 0000 0000 0000 0000 0000 0000
		// 3>>4: 0000 0000 0000 0000 0000 0000 0000 0000
		System.out.println("Binary: " + toUnsignedString(3 >> 4, 1));

		// 3: 0000 0000 0000 0000 0000 0000 0000 0011
		// 3>>>1: 0000 0000 0000 0000 0000 0000 0000 0001
		// 3>>>2: 0000 0000 0000 0000 0000 0000 0000 0000
		// 3>>>3: 0000 0000 0000 0000 0000 0000 0000 0000
		// 3>>>4: 0000 0000 0000 0000 0000 0000 0000 0000
		System.out.println("Binary: " + toUnsignedString(3 >>> 4, 1));
	}
}
