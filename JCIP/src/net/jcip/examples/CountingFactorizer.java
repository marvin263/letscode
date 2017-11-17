package net.jcip.examples;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.jcip.annotations.ThreadSafe;

/**
 * CountingFactorizer
 * 
 * Servlet that counts requests using AtomicLong
 * 
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class CountingFactorizer extends GenericServlet implements Servlet {
	//Marvin: 
	// 1. 将 thread-safty delegate给了 AtomicLong
	// 2. 如果 count 不 final：
	// 线程A：给 count 重新赋值时
	// 必须确保，线程B 能看到新来的reference（必须的synchronized，或者设成 volatile的） 
	private final AtomicLong count = new AtomicLong(0);

	public long getCount() {
		return count.get();
	}

	public void service(ServletRequest req, ServletResponse resp) {
		BigInteger i = extractFromRequest(req);
		BigInteger[] factors = factor(i);
		count.incrementAndGet();
		encodeIntoResponse(resp, factors);
	}

	void encodeIntoResponse(ServletResponse res, BigInteger[] factors) {
	}

	BigInteger extractFromRequest(ServletRequest req) {
		return null;
	}

	BigInteger[] factor(BigInteger i) {
		return null;
	}
}
