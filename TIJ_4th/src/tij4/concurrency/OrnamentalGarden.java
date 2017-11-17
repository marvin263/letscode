package tij4.concurrency;

//: concurrency/OrnamentalGarden.java
import static net.mindview.util.Print.print;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Count {
	private int count = 0;
	private Random rand = new Random(47);

	// Remove the synchronized keyword to see counting fail:
	public synchronized int increment() {
		// Marvin: DO NOT use field directly, instead:
		// 1. assign filed to a local variable
		// 2. operate this local variable
		// 3. assign local variable to field
		int temp = count;
		if (rand.nextBoolean()) // Yield half the time
			Thread.yield();
		return (count = ++temp);
	}

	public synchronized int value() {
		return count;
	}
}
// Marvin: 门肯定是有多个的
class Entrance implements Runnable {
	private static Count count = new Count();
	private static List<Entrance> entrances = new ArrayList<Entrance>();
	private int number = 0;
	// Doesn't need synchronization to read:
	private final int id;
	// Marvin: PLEASE NOTE, WE USE volatile HERE
	private static volatile boolean canceled = false;

	// Atomic operation on a volatile field:
	public static void cancel() {
		canceled = true;
	}

	public Entrance(int id) {
		this.id = id;
		// Keep this task in a list. Also prevents garbage collection of dead
		// tasks:
		entrances.add(this);
	}

	public void run() {
		while (!canceled) {
			// Marvin: 除非这里的 getValue()在其他thread中被调用，否者，这里完全没有必要同步嘛
			synchronized (this) {
				++number;
			}
			// 2. Increase count(synchronized increase)
			print(this + " Total: " + count.increment());
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				print("sleep interrupted");
			}
		}
		print("Stopping " + this);
	}

	public synchronized int getValue() {
		return number;
	}

	public String toString() {
		return "Entrance " + id + ": " + getValue();
	}

	public static int getTotalCount() {
		return count.value();
	}

	public static int sumEntrances() {
		int sum = 0;
		for (Entrance entrance : entrances)
			sum += entrance.getValue();
		return sum;
	}
}

public class OrnamentalGarden {
	public static void main(String[] args) throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++){
			exec.execute(new Entrance(i));
		}
		// Run for a while, then stop and collect the data:
		TimeUnit.SECONDS.sleep(3);
		//Marvin: main线程设置条件，从而使得 Entrance 线程都停下
		Entrance.cancel();
		// Marvin: exec不再接受新task，运行中的则停下来
		exec.shutdown();
		if (!exec.awaitTermination(250, TimeUnit.MILLISECONDS))
			print("Some tasks were not terminated!");
		// Marvin: 这里是在main线程中调用 Entrance.getValue()，这和 Entrance.run()是不同的线程。所以，Entrance.run()中，才同步更新
		print("Total: " + Entrance.getTotalCount());
		print("Sum of Entrances: " + Entrance.sumEntrances());
	}
} /*
 * Output: (Sample) Entrance 0: 1 Total: 1 Entrance 2: 1 Total: 3 Entrance 1: 1
 * Total: 2 Entrance 4: 1 Total: 5 Entrance 3: 1 Total: 4 Entrance 2: 2 Total: 6
 * Entrance 4: 2 Total: 7 Entrance 0: 2 Total: 8 ... Entrance 3: 29 Total: 143
 * Entrance 0: 29 Total: 144 Entrance 4: 29 Total: 145 Entrance 2: 30 Total: 147
 * Entrance 1: 30 Total: 146 Entrance 0: 30 Total: 149 Entrance 3: 30 Total: 148
 * Entrance 4: 30 Total: 150 Stopping Entrance 2: 30 Stopping Entrance 1: 30
 * Stopping Entrance 0: 30 Stopping Entrance 3: 30 Stopping Entrance 4: 30
 * Total: 150 Sum of Entrances: 150
 */// :~
