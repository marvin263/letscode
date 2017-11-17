package marvin.doit.exception_cause_low_performance;

public class UseCommonException {
	private RuntimeException e;

	public void createE() {
		e = new RuntimeException(Thread.currentThread().getName());
	}

	public RuntimeException getE() {
		return e;
	}

	public static void main(String[] args) throws Exception {
		final UseCommonException uce = new UseCommonException();
		uce.createE();

		Thread t = new Thread() {
			@Override
			public void run() {
				beCalled();
			}

			public void beCalled() {
				throw uce.e;
			}
		};
		t.start();
	}
}
