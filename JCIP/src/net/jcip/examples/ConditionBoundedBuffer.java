package net.jcip.examples;

import java.util.concurrent.locks.*;

import net.jcip.annotations.*;

/**
 * ConditionBoundedBuffer
 * <p/>
 * Bounded buffer using explicit condition variables
 * 
 * @author Brian Goetz and Tim Peierls
 */

@ThreadSafe
public class ConditionBoundedBuffer<T> {
	protected final Lock lock = new ReentrantLock();
	// CONDITION PREDICATE: notFull (count < items.length)
	private final Condition notFull = lock.newCondition();
	// CONDITION PREDICATE: notEmpty (count > 0)
	private final Condition notEmpty = lock.newCondition();
	private static final int BUFFER_SIZE = 100;
	@GuardedBy("lock")
	private final T[] items = (T[]) new Object[BUFFER_SIZE];
	@GuardedBy("lock")
	private int tail, head, count;

	// BLOCKS-UNTIL: notFull
	public void put(T x) throws InterruptedException {
		lock.lock();
		try {
			while (count == items.length) {
				// Marvin: 满了，放不进去了。等吧。。。其他地方，肯定有个 notFull.signal()
				notFull.await();
			}
			items[tail] = x;
			if (++tail == items.length)
				tail = 0;
			++count;
			// Marvin:里面至少有一个元素。发个通知吧（因为有人可能还在 notEmpty 上面等待着 ）
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	// BLOCKS-UNTIL: notEmpty
	public T take() throws InterruptedException {
		lock.lock();
		try {
			while (count == 0){
				// Marvin: 空了，啥都娶不到。等吧
				notEmpty.await();
			}
			T x = items[head];
			items[head] = null;
			if (++head == items.length)
				head = 0;
			--count;
			//Marvin: 用掉了一个。肯定不满了
			notFull.signal();
			return x;
		} finally {
			lock.unlock();
		}
	}
}
