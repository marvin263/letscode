public class Reluctant {
	private Reluctant internalInstance = new Reluctant();

	public Reluctant() throws Exception {
		throw new Exception("I'm not coming out");
	}

	public static void main(String[] args) {
		try {
			Reluctant b = new Reluctant();
			System.out.println("Surprise!");
		}
		// Marvin: 改成catch Throwable，就可以抓住了。就会打印出 "I told you so"
		catch (Throwable ex) {
			System.out.println("I told you so");
		}
	}
}
