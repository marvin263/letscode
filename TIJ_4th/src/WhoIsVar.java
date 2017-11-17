public class WhoIsVar extends BaseCls implements BaseIf {
	public static void main(String[] args) {
		System.out.println(BaseIf.myValue);
		System.out.println(myValue);
		System.out.println(new WhoIsVar().myValue);
		System.out.println(WhoIsVar.myValue);
	}
}
