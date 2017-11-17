package marvin.doit.man_you_know_clone.all_return_super_clone;

public class A2 extends A1 implements Cloneable {
//	@Override
//	public Object clone() throws CloneNotSupportedException {
//		return super.clone();
//	}

	public static void main(String[] args) throws CloneNotSupportedException {
		A2 orgn = new A2();

		A2 aCopy = (A2) orgn.clone();
		System.out.println(orgn.getClass() == aCopy.getClass());
	}
}
