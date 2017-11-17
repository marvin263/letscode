//: enumerated/Burrito.java
package tij4.enumerated;

import static tij4.enumerated.Spiciness.HOT;
import static tij4.enumerated.Spiciness.MEDIUM;
import static tij4.enumerated.Spiciness.NOT;

public class Burrito {
	Spiciness degree;

	public Burrito(Spiciness degree) {
		this.degree = degree;
	}

	public String toString() {
		return "Burrito is " + degree;
	}

	public static void main(String[] args) {
		System.out.println(new Burrito(NOT));
		System.out.println(new Burrito(MEDIUM));
		System.out.println(new Burrito(HOT));
	}
} /*
 * Output: Burrito is NOT Burrito is MEDIUM Burrito is HOT
 */// :~
