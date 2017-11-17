import java.nio.charset.Charset;

public class Iso8859 {
	public static final String ISO_8859 = "iso-8859-";

	public static void displayChar() {
		// 224 -- 1110 0000 -->-32
		byte b = (byte) -32;
		int total = 16;
		String str;
		for (int i = 1; i <= total; i++) {
			String charsetname = ISO_8859 + i;
			try {
				str = new String(new byte[] { b }, Charset.forName(charsetname));
			} catch (Exception e) {
				System.out.println("224--" + charsetname + "-->not supported");
				continue;
			}
			System.out.println("224--" + charsetname + "-->" + str);
		}
	}

	public static void main(String[] args) {
		displayChar();
	}

}
