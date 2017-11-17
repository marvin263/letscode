public class HelloGoodbye {
	public static void main(String[] args) {
		try {
			//Marvin: 真tmd的拧巴。好吧，你胜了，仅仅只有 HelloWorld
			System.out.println("Hello world");
			System.exit(0);
		} finally {
			System.out.println("Goodbye world");
		}
	}
}
