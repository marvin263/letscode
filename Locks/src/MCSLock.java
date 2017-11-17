import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class MCSLock {
	public static class MCSNode {
		volatile MCSNode next;
		volatile boolean isBlock = true; // 默认是在等待锁
	}

	volatile MCSNode queue;// 指向最后一个申请锁的MCSNode
	private static final AtomicReferenceFieldUpdater UPDATER = AtomicReferenceFieldUpdater
			.newUpdater(MCSLock.class, MCSNode.class, "queue");

	public void lock(MCSNode currentThread) {
		MCSNode predecessor = (MCSNode) UPDATER.getAndSet(this, currentThread);// step
																				// 1
		if (predecessor != null) {
			predecessor.next = currentThread;// step 2

			while (currentThread.isBlock) {// step 3
			}
		}
	}

	public void unlock(MCSNode currentThread) {
		if (currentThread.isBlock) {// 锁拥有者进行释放锁才有意义
			return;
		}

		if (currentThread.next == null) {// 检查是否有人排在自己后面
			if (UPDATER.compareAndSet(this, currentThread, null)) {// step 4
				// compareAndSet返回true表示确实没有人排在自己后面
				return;
			} else {
				// 突然有人排在自己后面了，可能还不知道是谁，下面是等待后续者
				// 这里之所以要忙等是因为：step 1执行完后，step 2可能还没执行完
				while (currentThread.next == null) { // step 5
				}
			}
		}

		currentThread.next.isBlock = false;
		currentThread.next = null;// for GC
	}
}
