package net.jcip.examples;

import java.util.concurrent.*;

/**
 * NoncancelableTask
 * <p/>
 * Noncancelable task that restores interruption before exit
 *
 * @author Brian Goetz and Tim Peierls
 */
public class NoncancelableTask {

	// Marvin: 这个方法 不支持 取消。这种情况下，必须用在 loop 中
	// 不支持 取消 哦，亲
    public Task getNextTask(BlockingQueue<Task> queue) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    interrupted = true;
                    // fall through and retry
                }
            }
        } finally {
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    interface Task {
    }
}
