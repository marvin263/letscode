package inheritance_reduce_throws_exception;
import java.io.IOException;

public class Arcane1 {
	public static void main(String[] args) {
		// Marvin: checked exception。try没抛出，catch断然不能瞎catch的。如果违反这条，则
		// compile-error
		// it is a compile-time error for a catch clause to catch a checked
		// exception type E if the corresponding try clause can't throw an
		// exception of some subtype of E [JLS 11.2.3]
		// Marvin: try没抛 checked exception，catch就不要下接 checked exception
		try {
			System.out.println("Hello world");
		} catch (IOException e) {
			System.out.println("I've never seen println fail!");
		}
	}
}
