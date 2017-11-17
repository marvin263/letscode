public class Super2 extends Super1 {
	public void display() {
		System.out.println("super2 self");
		super.display();
	}

	public static void main(String[] args) {
		Super2 s2 = new Super2();
		s2.display();
	}
}
