import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PracticeDump {
	public void display0() {
		display1();
	}

	public void display1() {
		display2();
	}

	public void display2() {
		System.out.println("I'm display(), finally display");
	}

	public void hellmoto() {
		System.out.println("Hello Moto");
	}

	public void doSomething() {
		final Object lock1 = new Object();
		final Object lock2 = new Object();

		Thread t1 = new Thread("Use lock1 first") {
			@Override
			public void run() {
				while (true) {
					System.out.println("t1: "
							+ Long.toHexString(Thread.currentThread().getId()));
					synchronized (lock1) {
						display0();
						synchronized (lock2) {
							hellmoto();
						}
					}
				}
			}
		};

		Thread t2 = new Thread("Use lock2 first") {
			@Override
			public void run() {

				while (true) {
					System.out.println("t2: "
							+ Long.toHexString(Thread.currentThread().getId()));
					synchronized (lock2) {
						display0();
						synchronized (lock1) {
							hellmoto();
						}
					}
				}
			}
		};
		final ReentrantLock rl = new ReentrantLock();
		Thread syncLock = new Thread("ReentrantLock1") {
			@Override
			public void run() {
				while (true) {
					rl.lock();
					try {
						Condition c = rl.newCondition();
						try {
							c.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} finally {
						rl.unlock();
					}
				}
			}
		};

		Thread tdeamon = new Thread("A Deamon Thread") {
			@Override
			public void run() {
				System.out.println("tdeamon: "
						+ Long.toHexString(Thread.currentThread().getId()));
				while (true) {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};

		tdeamon.setDaemon(true);
		tdeamon.start();
		t1.start();
		t2.start();
		syncLock.start();
	}

	public static void main(String[] args) {
		PracticeDump pd = new PracticeDump();
		pd.doSomething();
	}
}
