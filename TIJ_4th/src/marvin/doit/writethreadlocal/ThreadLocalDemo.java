package marvin.doit.writethreadlocal;

public class ThreadLocalDemo extends Thread {

	public static void main(String args[]) {

		Thread threadOne = new ThreadLocalDemo();
		threadOne.start();

		Thread threadTwo = new ThreadLocalDemo();
		threadTwo.start();
	}

	@Override
	public void run() {
		// sample code to simulate transaction id
		Context context = new Context();
		context.setTransactionId(getName());
		// Marvin: 必须先set。而且，任何一个线程（注意，是任何一个）都必须set一个值
		// set the context object in thread local to access it somewhere else
		MyThreadLocal.set(context);
		// Marvin: 任何一个线程（只要曾经set过），在执行流中任何地方（执行流中的任何一个方法），都可以得到这个值
		// “无需 以参数形式 传递这个值”，用的时候直接调用无参的 get() 即可。这正是精髓
		/* note that we are not explicitly passing the transaction id */
		new BusinessService().businessMethod_1();
		new BusinessService().businessMethod_2();
		MyThreadLocal.unset();

	}
}
