package net.jcip.examples;

/**
 * LeftRightDeadlock
 * 
 * Simple lock-ordering deadlock
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class LeftRightDeadlock {
	private final Object left = new Object();
	private final Object right = new Object();

	// Marvin: [1/4, Lock-ordering, cyclic lock]怎么破？线程A，线程B “得到两把锁的顺序” 必须一致
	// 线程A 调用 leftRight，得到 left来，正要得 right
	// 线程B 调用 rightLeft，得到 right来，正好得 left
	// 个儿屁，死锁
	// 为什么会死锁？根本原因是：线程A，线程B “两个线程，用不同的顺序，去得到同样的锁”
	// "attempt to acquire the same locks in a DIFFERENT order"。
	public void leftRight() {
		synchronized (left) {
			synchronized (right) {
				doSomething();
			}
		}
	}

	public void rightLeft() {
		synchronized (right) {
			synchronized (left) {
				doSomethingElse();
			}
		}
	}

	void doSomething() {
	}

	void doSomethingElse() {
	}
}
