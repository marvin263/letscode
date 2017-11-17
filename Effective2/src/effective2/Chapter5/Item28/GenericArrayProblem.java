package effective2.Chapter5.Item28;

public class GenericArrayProblem<E> {
	// Create an array of Object, and cast it to E[]
	public E[] createArray() {
		@SuppressWarnings("unchecked")
		E[] myArray = (E[]) new Object[16];
		return myArray;
	}

	public Object[] createObjectArray() {
		Object[] myArray = new Object[16];
		// 然后，留到每次 取出值 时去 转换
		return myArray;
	}
}
