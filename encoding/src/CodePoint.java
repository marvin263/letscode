public class CodePoint {
	public static void main(String[] args) {
		String gao = "高";
		String tie = "铁";
		System.out.println(gao + ": "
				+ Integer.toHexString(gao.codePointAt(0)).toUpperCase());
		System.out.println(tie + ": "
				+ Integer.toHexString(tie.codePointAt(0)).toUpperCase());

	}
}
