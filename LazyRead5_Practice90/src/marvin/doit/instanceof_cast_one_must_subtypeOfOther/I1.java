package marvin.doit.instanceof_cast_one_must_subtypeOfOther;

/**
 * Marvin: 两个事情
 * 
 * 1. null instanceof Object 时
 * 
 * the instanceof operator is defined to return false when its 1. left operand
 * is null（这保证：只要 true，则必定非null）
 * 
 * 2. instanceof 和 cast 时
 * 
 * the instanceof operator requires that if both operands are class types, one
 * must be a subtype of the other（无论谁是谁的 subtype，只要有subtype关系即可）
 * 
 * @author g705346
 * 
 */
public class I1 extends I0 {
	public static void main(String[] args) {
		I0 i0 = new I0();
		I1 i1 = new I1();
		System.out.println(null instanceof Object);
		System.out.println(i0 instanceof I1);
		System.out.println(i1 instanceof I0);

		I0 a0 = (I0) i1;
		I1 a1 = (I1) i0;
	}
}
