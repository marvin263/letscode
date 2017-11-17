package instanceof_cast;

public class Type1 {
	public static void main(String[] args) {
		// Marvin: instanceof, cast 。必须存在 父子类关系，就能使用
		System.out.println(null instanceof String);
		String s = null;
		// Marvin: Although null is subtype of every reference type,
		// the instanceof operator is defined to return false when its left
		// operator is null
		System.out.println(s instanceof String);
	}
}
