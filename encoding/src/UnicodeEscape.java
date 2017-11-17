public class UnicodeEscape {
	public static void main(String[] args) {
		// U+004D
		char[] c_004D = Character.toChars(0x004D);
		display("U+004D", c_004D);
		// U+0430
		char[] c_0430 = Character.toChars(0x0430);
		display("U+0430", c_0430);
		// U+10302
		char[] c_10302 = Character.toChars(0x10302);
		display("U+10302", c_10302);
		System.out.println(Integer.toHexString(
				(int) Character.highSurrogate(0x10302)).toUpperCase());
		System.out.println(Integer.toHexString(
				Character.codePointAt(c_10302, 1)).toUpperCase());
	}

	private static void display(String str, char[] carray) {
		for (int i = 0; i < carray.length; i++) {
			char c = carray[i];
			if (i == 0) {
				System.out.print(str + " --> ");
			}
			if (i == carray.length - 1) {
				System.out.println(c);
			} else {
				System.out.print(c + ",");
			}
		}
	}
}
