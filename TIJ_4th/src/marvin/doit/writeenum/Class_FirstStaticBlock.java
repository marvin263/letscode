package marvin.doit.writeenum;

public class Class_FirstStaticBlock {
	static{
		System.out.println("Static block");
	}
	
	Class_FirstStaticBlock(){
		System.out.println("In constructor");
	}
	
	public static void main(String[] args) {
		new Class_FirstStaticBlock();
	}
}
