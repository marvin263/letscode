package net.jcip.examples;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * InterruptBorrowedThread
 * <p/>
 * Scheduling an interrupt on a borrowed thread
 * 
 * Marvin: 
 * 
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class TimedRun1 {
	private static final ScheduledExecutorService cancelExec = Executors
			.newScheduledThreadPool(1);

	public static void timedRun(Runnable r, long timeout, TimeUnit unit) {
		final Thread taskThread = Thread.currentThread();
		cancelExec.schedule(new Runnable() {
			public void run() {
				taskThread.interrupt();
			}
		}, timeout, unit);
		// Marvin：有个定律：你要调用 t.interrupt()，前提是 你必须要知道t的 interruption policy 是什么
		// timedRun()方法可能会在任意线程中被执行。想想，这会发生什么？

		// If the task completes before the timeout, the cancellation task (that
		// interrupts the thread in which timedRun was called) could go off
		// after timedRun has returned to its caller.

		// 你根本不知道 你所在的这个任意线程是如何处理 tashThread.interrupt()的。
		// r 是在 taskThread 中运行的，假设 r 早早运行完成了，然后taskThread接着
		// 执行其他内容。悲剧马上就要发生了：cancelExec中的内容开始运行了，于是：
		// taskThread正在运行的内容很可能就被打断了

		r.run();
	}
}
