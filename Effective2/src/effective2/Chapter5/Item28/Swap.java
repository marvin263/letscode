package effective2.Chapter5.Item28;

// Private helper method for wildcard capture - Pages 139-140

import java.util.*;

public class Swap {
	public static void swap(List<?> list, int i, int j) {
		// Marvin: List<?>，除了null，啥都add不进去。怎么解决？
		// Generic method 来 capture wildcard（好吧，我也不知道怎么算 捕获wildcard）
		swapHelper(list, i, j);
	}

	// Private helper method for wildcard capture
	private static <E> void swapHelper(List<E> list, int i, int j) {
		list.set(i, list.set(j, list.get(i)));
	}

	public static void main(String[] args) {
		// Swap the first and last argument and print the resulting list
		List<String> argList = Arrays.asList(args);
		swap(argList, 0, argList.size() - 1);
		System.out.println(argList);
	}
}
