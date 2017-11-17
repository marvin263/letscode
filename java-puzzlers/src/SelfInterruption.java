/**
 * Marvin: hohoho，我对这个不要太了解哦
 * 
 * 
 *<a href="http://note.youdao.com/share/?id=500b816733b4b6281a4e058ad15216b7&type=note">Java线程中断</a>
 *
 *
 */
public class SelfInterruption {
	public static void main(String[] args) {
		Thread.currentThread().interrupt();

		if (Thread.interrupted()) {
			System.out.println("Interrupted: " + Thread.interrupted());
		} else {
			System.out.println("Not interrupted: " + Thread.interrupted());
		}
	}
}
