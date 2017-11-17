import javax.swing.JButton;
import javax.swing.JFrame;

public class Guess {

	public Guess() {
		System.out.println("Hello World");
		throw new RuntimeException("Guess, what will happen");
	}

	public static void main(String[] args) {
		JFrame frm = new JFrame();
		JButton btn = new JButton();
		btn.setText("click");
		frm.getContentPane().add(btn);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(400, 300);
		frm.setVisible(true);
	}
}
