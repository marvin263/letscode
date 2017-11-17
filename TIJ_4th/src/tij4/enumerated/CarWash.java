package tij4.enumerated;

//: enumerated/CarWash.java
import static net.mindview.util.Print.print;

import java.util.EnumSet;

public class CarWash {
	public enum Cycle {
		UNDERBODY {
			void action() {
				print("Spraying the underbody");
			}
		},
		WHEELWASH {
			void action() {
				print("Washing the wheels");
			}
		},
		PREWASH {
			void action() {
				print("Loosening the dirt");
			}
		},
		BASIC {
			void action() {
				print("The basic wash");
			}
		},
		HOTWAX {
			void action() {
				print("Applying hot wax");
			}
		},
		RINSE {
			void action() {
				print("Rinsing");
			}
		},
		BLOWDRY {
			void action() {
				print("Blowing dry");
			}
		};
		//Marvin: 各个不同 enum instance 会有不同，所以，流出来这个让各个 instance 自己实现
		abstract void action();
	}

	EnumSet<Cycle> cycles = EnumSet.of(Cycle.BASIC, Cycle.RINSE);

	public void add(Cycle cycle) {
		cycles.add(cycle);
	}

	public void washCar() {
		for (Cycle c : cycles)
			c.action();
	}

	public String toString() {
		return cycles.toString();
	}

	public static void main(String[] args) {
		CarWash wash = new CarWash();
		print(wash);
		wash.washCar();
		// Order of addition is unimportant:
		wash.add(Cycle.BLOWDRY);
		wash.add(Cycle.BLOWDRY); // Duplicates ignored
		wash.add(Cycle.RINSE);
		wash.add(Cycle.HOTWAX);
		print(wash);
		wash.washCar();
	}
} /*
 * Output: [BASIC, RINSE] The basic wash Rinsing [BASIC, HOTWAX, RINSE, BLOWDRY]
 * The basic wash Applying hot wax Rinsing Blowing dry
 */// :~
