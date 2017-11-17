package effective2.Chapter5.Item28;

import java.util.ArrayList;
import java.util.List;

public class TestUnboundedWildcards {

	interface Function<T> {
		T apply(T arg1, T arg2);
	}

	public static void swap(List<?> list, int i, int j) {
		swapHelper(list, i, j);
	}

	static <E> E reduce(List<E> list, Function<E> f, E initVal) {

		ArrayList<String>[] arrayString = new ArrayList<String>[5];
		ArrayList<E>[] arrayE = new ArrayList<E>[5];
		E[] array = new E[5];

		// Cannot convert from Object[] to E[]
		E[] snapshot = (E[]) list.toArray();
		E result = initVal;
		for (E e : snapshot) {
			result = f.apply(result, e);
		}
		return result;
	}

	// capture the type。这个方法 就可以用来 捕获类型。。。不懂，啥是 “捕获类型”
	private static <E> void swapHelper(List<E> list, int i, int j) {
		list.set(i, list.set(j, list.get(i)));

		List<E> myList = null;
		@SuppressWarnings("null")
		E[] myArray = (E[]) myList.toArray();

	}

	public static void main(String[] args) {
		List<String> listStr = new ArrayList<String>();
		List<Object> listObject = new ArrayList<Object>();
		List listRaw = new ArrayList();
		List<?> listWildcard = new ArrayList<Object>();

		listWildcard = listStr;
		listWildcard = listObject;
		listWildcard = listRaw;

		listRaw = listStr;
		listRaw = listObject;
		listRaw = listWildcard;

		listObject = listStr;

	}
}
