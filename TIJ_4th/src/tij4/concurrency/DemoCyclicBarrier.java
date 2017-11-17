package tij4.concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DemoCyclicBarrier {
	private CyclicBarrier barrier;
	private int loopCount = 10;
	private volatile int total = 0;

	public DemoCyclicBarrier(int threadCount) {
		barrier = new CyclicBarrier(threadCount, new Runnable() {
			@Override
			public void run() {
				collectionTestResult();
			}
		});

		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread("test-thread" + i) {
				public void run() {
					for (int j = 0; j < loopCount; j++) {
						doTest();
						System.out.println("Current loop:" + j + ", total:"
								+ total);
						try {
							// Marvin：这里对 CyclicBarrier的使用，正的体现了 Cyclic 的含义
							barrier.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (BrokenBarrierException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();
		}

	}

	public void collectionTestResult() {
		System.out.println("total=" + total);
	}

	public void doTest() {
		total++;
	}

	public static void main(String[] args) {
		new DemoCyclicBarrier(8);
	}
}
