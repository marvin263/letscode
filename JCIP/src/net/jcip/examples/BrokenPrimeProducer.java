package net.jcip.examples;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * BrokenPrimeProducer
 * <p/>
 * Unreliable cancellation that can leave producers stuck in a blocking
 * operation
 * 
 * @author Brian Goetz and Tim Peierls
 */
abstract class BrokenPrimeProducer extends Thread {
	private final BlockingQueue<BigInteger> queue;
	private volatile boolean cancelled = false;

	BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
		this.queue = queue;
	}

	public void run() {
		try {
			BigInteger p = BigInteger.ONE;
			while (!cancelled) {
				queue.put(p = p.nextProbablePrime());
			}
		} catch (InterruptedException consumed) {
		}
	}

	public void cancel() {
		cancelled = true;
	}

	void consumePrimes() throws InterruptedException {
		BlockingQueue<BigInteger> primes = null;// only for demo
		BrokenPrimeProducer producer = null;// only for demo. Marvin: 注意，producer是一个新的线程
		producer.start();
		try {
			while (needMorePrimes())
				consume(primes.take());
		} finally {
			// Marvin:
			// 线程A中：调用 producer.cancel()，然后 线程B 也看到了 cancelled 变成了 false。一切都很美好的样子
			// 但是，线程A 使得 cancelled变成false时，线程B 正被 put() block着
			// 悲剧发生了：线程B永远没有机会看到 cancelled 变成 false，也就是说，线程B永远都不会结束了
			producer.cancel();
		}
	}

	public abstract boolean needMorePrimes();

	public abstract void consume(BigInteger bi);
}
