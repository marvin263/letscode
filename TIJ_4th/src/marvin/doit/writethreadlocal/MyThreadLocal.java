package marvin.doit.writethreadlocal;

/**
 * Marvin: <b>这个类中的一切，都是 static 的</b>
 * 
 * this class acts as a container to our thread local variables.
 * 
 * @author vsundar
 * 
 */
public class MyThreadLocal {
	public static final ThreadLocal<Context> userThreadLocal = new ThreadLocal<Context>();

	// Marvin: 对于key（userThreadLocal），可以关联到一个value（user）
	// 注意：当然每个线程，都必须执行到这个方法。否则，线程中不会有这个值的
	public static void set(Context user) {
		userThreadLocal.set(user);
	}

	public static void unset() {
		userThreadLocal.remove();
	}

	// Marvin: 这正是 threadlocal 牛逼所在，get()时，无需如何参数，直接无参 get() 即可
	// 对于 java.lang.Thread 而言，userThreadLocal就是
	// key，既然有key了，当然可以直接获取value啦（set设置进去的）
	public static Context get() {
		return userThreadLocal.get();
	}
}
