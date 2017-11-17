package marvin.doit.writeenum;

public class StaticSelf {
	public static final StaticSelf one;
	public static final StaticSelf two;
	public static final StaticSelf three;
	
	static{
		one = new StaticSelf();
		two = new StaticSelf();
		three = new StaticSelf();
		System.out.println("Static block");
	}
	public StaticSelf(){
		System.out.println("In constructor");
	}
	
	public static void main(String[] args) {
		System.out.println(StaticSelf.class);
	}
}
