package marvin.doit.jvm_invoke_clinit;

public class Son extends Father {
	static {
		System.out.println("Son static block");
	}

	public Son() {
		System.out.println("Son constructor");
	}
	// 1. <clinit()> 总是被 JVM 所调用
	// 2. JVM 调用某个 <clinit()>前，总是确认：超类的 <clinit()> 已经执行过了
	public static void main(String[] args) {
		new Son();
		// Father static block
		// Son static block
		// Father constructor
		// Son constructor
	}
}
