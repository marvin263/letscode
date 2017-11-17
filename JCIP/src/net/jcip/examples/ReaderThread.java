package net.jcip.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * ReaderThread
 * <p/>
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class ReaderThread extends Thread {
	private static final int BUFSZ = 512;
	private final Socket socket;
	private final InputStream in;

	public ReaderThread(Socket socket) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
	}

	public void interrupt() {
		try {
			socket.close();
		} catch (IOException ignored) {
		} finally {
			super.interrupt();
		}
	}

	public void run() {
		try {
			byte[] buf = new byte[BUFSZ];
			while (true) {
				int count = in.read(buf);
				if (count < 0)
					break;
				else if (count > 0)
					processBuffer(buf, count);
			}
		} catch (IOException e) { /* Allow thread to exit */
			// Marvin: 线程B 调用了rt.interrupt()，从而进入了这里
			// 线程B调用 rt.interrupt()时，无论 rt 目前 blocking 在何处，总是可以响应的
			// blocking 在 in.read()/out.write() 时，根本就不响应 interrupt(); 但是，我们这里覆写了
			// Thread的interrupt()，从而使得总是可以 响应 interrupt()

		}
	}

	public void processBuffer(byte[] buf, int count) {
	}
}
