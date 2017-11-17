package net.jcip.examples;

/**
 * NoVisibility
 * <p/>
 * Sharing variables without synchronization
 * 
 * @author Brian Goetz and Tim Peierls
 */

public class NoVisibility {
	private static boolean ready;
	private static int number;

	private static class ReaderThread extends Thread {
		public void run() {
			while (!ready)
				Thread.yield();
			System.out.println(number);
		}
	}

	// Marvin: 正常的理解是：ReaderThread会一直停止着
	// main thread 给 ready赋值true
	// 后，ReaderThread才能进行。那个时候，number也肯定就是42(因为number先于ready执行)

	// 但是，多线程环境下，不一定的。有可能 ReaderThread看到了ready==true，number却依然看到的是 0（reorder或cpu
	// cache都会导致这种情况）
	public static void main(String[] args) {
		new ReaderThread().start();
		number = 42;
		ready = true;
	}
}
