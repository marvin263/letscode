/**
 * @author marvin
 * 
 */
public class TenureDirectly {
	private static final int _1M = 1024 * 1024;

	/**
	 * 
	 * <pre>
	 * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC -XX:PretenureSizeThreshold=3145728
	 * </pre>
	 * 
	 * 1. -XX:PretenureSizeThreshold 设置为 3M，超过 3M 的对象，直接在 Tenured gen 分配
	 * 
	 * 所以，我们会看到，default new gen 几乎没有被占用
	 * 
	 */
	@SuppressWarnings("unused")
	public static void testPretenureSizeTHreshold() {
		byte[] allocation1;
		System.out.println("Begin 3M");
		allocation1 = new byte[4 * _1M];
		System.out.println("After 3M");
	}

	public static void main(String[] args) {
		testPretenureSizeTHreshold();
	}
}
