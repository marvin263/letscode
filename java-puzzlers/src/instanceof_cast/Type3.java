package instanceof_cast;


public class Type3 {
	public static void main(String args[]) {
		// Marvin: instanceof, cast 。必须存在 父子类关系，就能使用
		// Marvin: 只要存在 父子类关系，则cast总是成功; null是所有类型的 subtype，所以总是可以任意 cast
		String str = (String) null;
		Integer itg = (Integer) null;
		java.sql.Date sqlDate = (java.sql.Date) null;

		// Marvin: Object--to be cast type; Type3: cast type
		// Like the instanceof operation, if both types in a cast operation are
		// class types, one must be a subtype of the other（向上，向下转型都是允许的）
		Type3 t1 = (Type3) new Object();
		// 不存在父子类关系，变异时错误
		Type3 t2 = (Type3) new String();
	}
}
