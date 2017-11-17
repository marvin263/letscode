package net.jcip.examples;

import net.jcip.annotations.NotThreadSafe;

/**
 * UnsafeSequence
 * 
 * @author Brian Goetz and Tim Peierls
 */

@NotThreadSafe
public class UnsafeSequence {
	private int value;

	/**
	 * Returns a unique value.
	 */
	public int getNext() {
		// Marvin:这条看起来很紧凑的语句，并不是 原子操作
		// 1.fetch current value
		// 2.add one to it
		// 3.write the new value back
		return value++;
	}
}
