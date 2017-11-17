public class CoreCode {
	public void corePrint() {
		System.out.println("CodeCode: " + this.getClass().getClassLoader());
		try {
			Object obj = Class.forName("AppCode", true,
					Thread.currentThread().getContextClassLoader())
					.newInstance();
			System.out.println("Reflection AppCode: " + obj.getClass());
			((AppCode) obj).appPrint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
