package net.jcip.examples;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * SafePoint
 * 
 * @author BrigF5Dh3ac5vh5an Goetz and Tim Peierls
 */
@ThreadSafe
public class SafePoint {
	@GuardedBy("this")
	private int x, y;

	private SafePoint(int[] a) {
		this(a[0], a[1]);
	}

	public SafePoint(SafePoint p) {
		this(p.get());
	}

	public SafePoint(int x, int y) {
		this.set(x, y);
	}

	// Marvin:如果不返回数组，而是提供 getX(), getY()，那么，即使这两个方法都 synchronized，也是不行的
	// 线程A getX()，getY()，但是，这两次调用之间，线程B调用 set(x, y) 重新设置了Y

	// 于是：线程A 得到的 (x, y)，很可能就是一个错误的 point
	// 当然，如果 x,y都是final的，那就没啥问题了。线程B 永远都不能更改 Y
	public synchronized int[] get() {
		return new int[] { x, y };
	}

	public synchronized void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
