/**
 * Exception in thread "main" java.lang.StackOverflowError
 * 
 * @author marvin
 */
public class Singleton {
	private Singleton INSTANCE = new Singleton();

	public Singleton() {
	}

	public static void main(String[] args) {
		Singleton s = new Singleton();
		System.out.println(s.toString());
	}
}
