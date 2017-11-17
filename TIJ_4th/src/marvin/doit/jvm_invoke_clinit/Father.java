package marvin.doit.jvm_invoke_clinit;

public class Father {
	static{
		System.out.println("Father static block");
	}
	public Father(){
		System.out.println("Father constructor");
	}
}
