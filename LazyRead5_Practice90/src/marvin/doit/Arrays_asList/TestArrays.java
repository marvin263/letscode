package marvin.doit.Arrays_asList;

import java.util.Arrays;
import java.util.List;

/**
 * 两件事情
 * 
 * 1. Arrays.asList() 不能作用于 primitive 类型的数组 Arrays.asList(new int[]{1, 2, 3});
 * 不行。。。。。（int[], long[], boolean[] 等），因为这种情况下，返回的是：List<int[]>, List<long[]>
 * 
 * 2. String... varargs 在调用时，会先生成一个 String[]的数组
 * 
 * @author g705346
 * 
 */
public class TestArrays {
	public static void main(String[] args) {
		Object[] arrayObj = { "String", new Object(), new Object() };
		int[] arrayInt = { 0, 1, 2 };

		List<Object> list = Arrays.asList(arrayObj);
		List<int[]> listInt = Arrays.asList(arrayInt);
		System.out.println(list);
		System.out.println(listInt);

		String[] arrayStr = { "str 0", "str 1", "str 2" };
		useArray(arrayStr);
		useVarargs(arrayStr);
	}

	public static void useArray(String[] arrayStr) {
		for (String str : arrayStr) {
			System.out.println(str);
		}
	}

	public static void useVarargs(String... varargs) {
		for (String str : varargs) {
			System.out.println(str);
		}
	}
}
