public class Utf8 {
	public static void main(String[] args) {
		String zhong = "中";
		System.out.println("length:" + zhong.length());

		int cpc = zhong.codePointCount(0, zhong.length());
		System.out.println("code point count:" + cpc);

		for (int i = 0; i < cpc; i++) {
			System.out.println("CodePoint-" + i + ": U+"
					+ Integer.toHexString(zhong.codePointAt(i)).toUpperCase());
		}
	}
}
