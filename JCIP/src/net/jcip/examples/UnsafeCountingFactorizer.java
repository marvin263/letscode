package net.jcip.examples;

import java.math.BigInteger;

import net.jcip.annotations.NotThreadSafe;

/**
 * UnsafeCountingFactorizer
 * 
 * Servlet that counts requests without the necessary synchronization
 * 
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class UnsafeCountingFactorizer extends GenericServlet implements Servlet {
	private long count = 0;

	public long getCount() {
		return count;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		BigInteger i = extractFromRequest(req);
		BigInteger[] factors = factor(i);
		// Marvin:紧凑的语法会使你认为，这是一条原子语句。事实并非如此
		// 1.fetch current value
		// 2.add one to it
		// 3.write the new value back
		// Marvin: 此处即有 race condition 也有 data race
		// 上面的应该叫做：read-modify-write 类型的 race condition
		// （data race 是指：线程B看不到线程A的更改，虽然线程A确实修改过了）
		++count;
		encodeIntoResponse(resp, factors);
	}

	void encodeIntoResponse(ServletResponse res, BigInteger[] factors) {
	}

	BigInteger extractFromRequest(ServletRequest req) {
		return new BigInteger("7");
	}

	BigInteger[] factor(BigInteger i) {
		// Doesn't really factor
		return new BigInteger[] { i };
	}
}
