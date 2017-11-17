package net.jcip.examples;

/**
 * InduceLockOrder
 * 
 * Inducing a lock order to avoid deadlock
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class InduceLockOrder {
	private static final Object tieLock = new Object();

	// Marvin: [2/4, Dynamic Lock Order] 怎么破？我勒个去，竟然可以这么解决，NB。
	// 这种解决方法叫做：induce lock order。Marvin把这叫做 “引入 锁顺序”的锁定
	// Warning: deadlock-prone!
	public void transferMoney(final Account fromAcct, final Account toAcct,
			final DollarAmount amount) throws InsufficientFundsException {
		class Helper {
			public void transfer() throws InsufficientFundsException {
				if (fromAcct.getBalance().compareTo(amount) < 0)
					throw new InsufficientFundsException();
				else {
					fromAcct.debit(amount);
					toAcct.credit(amount);
				}
			}
		}
		int fromHash = System.identityHashCode(fromAcct);
		int toHash = System.identityHashCode(toAcct);

		if (fromHash < toHash) {
			synchronized (fromAcct) {
				synchronized (toAcct) {
					new Helper().transfer();
				}
			}
		} else if (fromHash > toHash) {
			synchronized (toAcct) {
				synchronized (fromAcct) {
					new Helper().transfer();
				}
			}
		} else {
			synchronized (tieLock) {
				synchronized (fromAcct) {
					synchronized (toAcct) {
						new Helper().transfer();
					}
				}
			}
		}
	}

	interface DollarAmount extends Comparable<DollarAmount> {
	}

	interface Account {
		void debit(DollarAmount d);

		void credit(DollarAmount d);

		DollarAmount getBalance();

		int getAcctNo();
	}

	class InsufficientFundsException extends Exception {
	}
}
