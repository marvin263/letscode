package marvin.doit.exception_cause_low_performance;

/**
 * Marvin：两件事情
 * 
 * 1. native 的 fillInStackTrace() 需要 “which looks down the stack (before the actual
 * throw) and puts a whole backtrace into the exception object” 这正是此方法较慢的原因
 * 
 * 2. 正是此方法 将 当前线程的栈帧信息 记录到 Throwable 对象中。如果，此方法不作这些事情，则：线程的栈帧信息 无处可寻
 * 
 * @author g705346
 * 
 */
public class JustForDemoException extends RuntimeException {
	private static final long serialVersionUID = 2134186859756705446L;

	/*
	 * @Override public Throwable fillInStackTrace() { return this; }
	 */

	private static void method1() {
		System.out.println("in method 1");
		method2();
	}

	private static void method2() {
		System.out.println("in method 2");
		method3();
	}

	private static void method3() {
		System.out.println("in method 3");
		method4();
	}

	private static void method4() {
		System.out.println("in method 4");
		throw new JustForDemoException();
	}

	public static void main(String[] args) {
		method1();
	}

}
