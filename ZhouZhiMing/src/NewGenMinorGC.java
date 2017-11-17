/**
 * @author marvin
 * 
 */
public class NewGenMinorGC {
	private static final int _1M = 1024 * 1024;

	/**
	 * 
	 * <pre>
	 * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC
	 * </pre>
	 * 
	 * 1. -Xmn 新生代 分了10M（Eden 占了 8M, from 1M, to 1M）
	 * 
	 * 分配对象 只能分配到 Eden 上。所以，allocation4 > 2M，则，必会有 Minor GC
	 * 
	 * 不明白：" compacting perm gen  total 16384K, used 1772K"，pern gen这些内存是在那里分配的？
	 * 不是在 heap 上吧？ 因为：new/tenured generation 之和已经是 所有 heap 内存了！
	 * 
	 * 那么，在哪里分配的呢？现在这个java进程的内存应该是：perm gen + new gen + tenured gen=16+10+10=36M
	 * 
	 * 
	 */
	@SuppressWarnings("unused")
	public static void testAllocation() {
		byte[] allocation1, allocation2, allocation3, allocation4;
		allocation1 = new byte[2 * _1M];
		allocation2 = new byte[2 * _1M];
		allocation3 = new byte[2 * _1M];

		System.out.println("Begin 2M");
		allocation4 = new byte[2 * _1M];
		System.out.println("After 2M");
	}

	public static void main(String[] args) {
		testAllocation();
	}
}
