package marvin.doit.reminder_sign_as_leftOperand;

/**
 * Marvin:
 * 
 * 余数 总是和 左操作数 符号保持一致
 * 
 * when the remainder operation returns a nonzero result, it has the same sign
 * as its left operand
 * 
 * @author g705346
 * 
 */
public class ReminderSign {
	public static void main(String[] args) {
		int a = -5, b = -3, c = 3;
		//
		System.out.println(a % b);//-2
		System.out.println(a % c);//-2
	}
}
