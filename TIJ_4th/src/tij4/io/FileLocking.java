package tij4.io;

//: io/FileLocking.java
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

public class FileLocking {
	public static void main(String[] args) throws Exception {
		FileOutputStream fos = new FileOutputStream("file.txt");
		FileLock fl = fos.getChannel().tryLock();
		if (fl != null) {
			System.out.println("Locked File");
			TimeUnit.MILLISECONDS.sleep(155500);
			fl.release();
			System.out.println("Released Lock");
		}
		fos.close();
	}
} /*
 * Output: Locked File Released Lock
 */// :~
