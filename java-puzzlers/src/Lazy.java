public class Lazy {
	private static boolean initialized = false;
	// Marvin: 线程A 用到一个类，则，线程A 就必须负责将此类 initialization
	// 一个类只能被 initialization 一次
	// 当 线程A initializing 此类 时，线程B 也用到此类，则：线程B 就必须得等着，一直等到 线程A 将那个类
	// initialization 完成

	// 下面的程序中：
	// 1. main线程 负责 initialize Lazy.class

	// 2. main线程 initializing 时，中间必须停下来 等待 t线程 完成（ t.join(), t继续运行 ）

	// 3. 不幸的是，t线程 也用到 Lazy.class，然后，t线程
	// 发现："main线程 已经开始 initializing Lazy.class 啦"

	// 4. 于是，t线程 不去主动 initialize Lazy.class，而是：等待
	// "main线程 完成 initializing Lazy.class"

	// step 2（main等待t）; step 4（t等待main）。于是，死锁了

	static {
		Thread t = new Thread(new Runnable() {
			public void run() {
				initialized = true;
			}
		});
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}

	}

	public static void main(String[] args) {
		System.out.println(initialized);
	}
}
