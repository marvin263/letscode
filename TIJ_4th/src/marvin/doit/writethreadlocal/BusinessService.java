package marvin.doit.writethreadlocal;

public class BusinessService {

	public void businessMethod_1() {
		// get the context from thread local
		Context context = MyThreadLocal.get();
		System.out.println("businessMethod_1: " + context.getTransactionId());
	}

	public void businessMethod_2() {
		// get the context from thread local
		Context context = MyThreadLocal.get();
		System.out.println("businessMethod_2: " + context.getTransactionId());
	}
}
