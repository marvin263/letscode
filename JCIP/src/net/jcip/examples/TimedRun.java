package net.jcip.examples;

import static net.jcip.examples.LaunderThrowable.launderThrowable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TimedRun
 * <p/>
 * Cancelling a task using Future
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class TimedRun {
	private static final ExecutorService taskExec = Executors
			.newCachedThreadPool();

	public static void timedRun(Runnable r, long timeout, TimeUnit unit)
			throws InterruptedException {
		Future<?> task = taskExec.submit(r);
		try {
			task.get(timeout, unit);
		} catch (TimeoutException e) {
			// task will be cancelled below
		} catch (ExecutionException e) {
			// exception thrown in task; rethrow
			throw launderThrowable(e.getCause());
		} finally {
			// Harmless if task already completed
			task.cancel(true); // interrupt if running
		}
	}
}
