package net.jcip.examples;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * PrimeGenerator
 * <p/>
 * Using a volatile field to hold cancellation state
 * 
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class PrimeGenerator implements Runnable {
	private static ExecutorService exec = Executors.newCachedThreadPool();

	@GuardedBy("this")
	private final List<BigInteger> primes = new ArrayList<BigInteger>();
	private volatile boolean cancelled;

	public void run() {
		BigInteger p = BigInteger.ONE;
		// Marvin: cancelled volatile，这当然是必须的
		while (!cancelled) {
			p = p.nextProbablePrime();
			// Marvin: primes 这个list会有多个线程访问，这里是 add()；后面其他地方还有 get()；
			// Marvin: 所以，对primes的访问，也都同步起来啦
			synchronized (this) {
				primes.add(p);
			}
		}
	}
	//Marvin: 当然，这是由其他线程来调用的
	public void cancel() {
		cancelled = true;
	}

	// Marvin: primes这个list，会有多个线程访问，有的add()，这里get()
	// Marvin: 如果你很不专业的说，我这里仅仅是访问啊，无需同步。只要 add()时同步不就行了嘛？
	// 这么说就太业余了，因为 synchronized写的 变量，你只有 再synchronized读 才能建立起 happen-before 关系。读时不加毛用没有
	public synchronized List<BigInteger> get() {
		return new ArrayList<BigInteger>(primes);
	}

	static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
		PrimeGenerator generator = new PrimeGenerator();
		exec.execute(generator);
		try {
			SECONDS.sleep(1);
		} finally {
			//Marvin: generator这个Runnable对象，是在 exec中执行的。然后，generator中的 cancel()方法是在另外的线程中执行的
			generator.cancel();
		}
		return generator.get();
	}
}
