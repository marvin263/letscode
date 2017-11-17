public class PingPong {
	// Marvin: main() 和 pong() 都使用了 "PingPong.class 的 class lock"
	// 调用main()的线程 和 调用pong()的线程 谁拥有 "PingPong.class 的 class lock" 谁才能运行
	// 然后，等先运行的那个方法 运行完毕后（释放了 class lock），另一个方法才可以运行
	public static synchronized void main(String[] a) throws Exception {
		Thread t = new Thread() {
			public void run() {
				pong();
			}
		};
		t.start();
		Thread.sleep(2000);
		System.out.print("Ping");

	}

	static synchronized void pong() {
		System.out.print("Pong");
	}
}
