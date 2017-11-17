//: enumerated/RoShamBo1.java
// Demonstration of multiple dispatching.
package tij4.enumerated;

import static tij4.enumerated.Outcome.DRAW;
import static tij4.enumerated.Outcome.LOSE;
import static tij4.enumerated.Outcome.WIN;

import java.util.Random;

interface Item {
	Outcome compete(Item it);

	Outcome eval(Paper p);

	Outcome eval(Scissors s);

	Outcome eval(Rock r);
}

class Paper implements Item {
	public Outcome compete(Item it) {
		return it.eval(this);
	}

	public Outcome eval(Paper p) {
		return DRAW;
	}

	public Outcome eval(Scissors s) {
		return WIN;
	}

	public Outcome eval(Rock r) {
		return LOSE;
	}

	public String toString() {
		return "Paper";
	}
}

class Scissors implements Item {
	public Outcome compete(Item it) {
		return it.eval(this);
	}

	public Outcome eval(Paper p) {
		return LOSE;
	}

	public Outcome eval(Scissors s) {
		return DRAW;
	}

	public Outcome eval(Rock r) {
		return WIN;
	}

	public String toString() {
		return "Scissors";
	}
}

class Rock implements Item {
	public Outcome compete(Item it) {
		// Marvin: 注意，这里的调用，是 it实例 的方法，而不是当前实例的方法
		return it.eval(this);
	}

	public Outcome eval(Paper p) {
		return WIN;
	}

	public Outcome eval(Scissors s) {
		return LOSE;
	}

	public Outcome eval(Rock r) {
		return DRAW;
	}

	public String toString() {
		return "Rock";
	}
}

public class RoShamBo1 {
	static final int SIZE = 20;
	private static Random rand = new Random(47);

	public static Item newItem() {
		switch (rand.nextInt(3)) {
		default:
		case 0:
			return new Scissors();
		case 1:
			return new Paper();
		case 2:
			return new Rock();
		}
	}

	public static void match(Item a, Item b) {
		// Marvin：两路分发。
		// 1. a 的实际类型，决定走到 谁的 compete 方法
		// 2. 进入 a.compete(b)，b.eval()是第二次 分发
		System.out.println(a + " vs. " + b + ": " + a.compete(b));
	}

	public static void main(String[] args) {
		for (int i = 0; i < SIZE; i++) {
			match(newItem(), newItem());
		}
	}
}
