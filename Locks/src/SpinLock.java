import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {
	private AtomicReference<Thread> owner = new AtomicReference<Thread>();

	public void lock() {
		Thread currentThread = Thread.currentThread();

		// 如果锁未被占用，则设置当前线程为锁的拥有者
		// Marvin：不停的循环，所以是 spin lock
		while (owner.compareAndSet(null, currentThread)) {
		}
	}

	public void unlock() {
		Thread currentThread = Thread.currentThread();

		// 只有锁的拥有者才能释放锁
		owner.compareAndSet(currentThread, null);
	}
}