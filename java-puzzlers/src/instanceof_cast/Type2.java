package instanceof_cast;
public class Type2 {
	public static void main(String[] args) {
		// Marvin: instanceof, cast 都要求：必须存在 父子类关系 时才能编译
		// the instanceof operator requires that if both operands are class
		// types, one must be a subtype of the other[JLS 15.20.2, 15.15, 5.5]
		// 不存在父子类关系，编译时错误
		System.out.println(new Type2() instanceof String);
	}
}
