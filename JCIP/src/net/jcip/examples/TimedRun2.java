package net.jcip.examples;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TimedRun2
 * <p/>
 * Interrupting a task in a dedicated thread
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class TimedRun2 {
	private static final ScheduledExecutorService cancelExec = newScheduledThreadPool(1);

	public static void timedRun(final Runnable r, long timeout, TimeUnit unit)
			throws InterruptedException {
		class RethrowableTask implements Runnable {
			private volatile Throwable t;

			public void run() {
				try {
					r.run();
				} catch (Throwable t) {
					this.t = t;
				}
			}

			void rethrow() {
				if (t != null)
					throw launderThrowable(t);
			}
		}

		RethrowableTask task = new RethrowableTask();
		final Thread taskThread = new Thread(task);
		taskThread.start();
		cancelExec.schedule(new Runnable() {
			public void run() {
				taskThread.interrupt();
			}
		}, timeout, unit);
		// Marvin：taskThread 继续执行
		// 当前 主调线程 阻塞在此地，等着 taskThread运行完成
		taskThread.join(unit.toMillis(timeout));
		task.rethrow();
	}
}
