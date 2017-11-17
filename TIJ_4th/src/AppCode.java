public class AppCode {

	static {
		System.out.println("static AppCode: " + AppCode.class.getClassLoader());
	}
	public void appPrint(){
		System.out.println("appPrint() AppCode: " + AppCode.class.getClassLoader());
	}
	public static void main(String[] args) throws Exception {
		CoreCode cc = new CoreCode();
		cc.corePrint();
	}
}
