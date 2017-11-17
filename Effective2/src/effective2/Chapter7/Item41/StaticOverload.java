package effective2.Chapter7.Item41;
// Broken! - What does this program print?

import java.util.*;
import java.math.*;

public class StaticOverload {
    public static String classify(Set<?> s) {
        return "Set";
    }

    public static String classify(List<?> lst) {
        return "List";
    }

    public static String classify(Collection<?> c) {
        return "Unknown Collection";
    }

    public static void main(String[] args) {
		Set<?> set = new HashSet<Boolean>();
		List<?> list = new ArrayList<BigInteger>();
		Collection<?> c = new HashMap<String, String>().values();

		Collection<?> set_Collection = new HashSet<Boolean>();
		Collection<?> list_Collection = new ArrayList<BigInteger>();
		Collection<?> c_Collection = new HashMap<String, String>().values();

		System.out.println(classify(set));
		System.out.println(classify(list));
		System.out.println(classify(c));

		System.out.println(classify(set_Collection));
		System.out.println(classify(list_Collection));
		System.out.println(classify(c_Collection));
    }
}
