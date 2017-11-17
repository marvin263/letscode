package tij4.concurrency;

//: concurrency/IntGenerator.java

public abstract class IntGenerator {
	// Marvin: It's just this keyword--VOLATILE that make the changes done by T1
	// be seen immediately by T2 as soon as it is changed
	private volatile boolean canceled = false;

	public abstract int next();

	// Allow this to be canceled:
	public void cancel() {
		canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}
} // /:~
