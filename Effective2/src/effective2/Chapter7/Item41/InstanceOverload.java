package effective2.Chapter7.Item41;

// Broken! - What does this program print?

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstanceOverload {
	public String classify(Set<?> s) {
		return "Set";
	}

	public String classify(List<?> lst) {
		return "List";
	}

	public String classify(Collection<?> c) {
		return "Unknown Collection";
	}

	public static void main(String[] args) {
		InstanceOverload iol = new InstanceOverload();
		Set<?> set = new HashSet<Boolean>();
		List<?> list = new ArrayList<BigInteger>();
		Collection<?> c = new HashMap<String, String>().values();

		Collection<?> set_Collection = new HashSet<Boolean>();
		Collection<?> list_Collection = new ArrayList<BigInteger>();
		Collection<?> c_Collection = new HashMap<String, String>().values();

		System.out.println(iol.classify(set));
		System.out.println(iol.classify(list));
		System.out.println(iol.classify(c));

		System.out.println(iol.classify(set_Collection));
		System.out.println(iol.classify(list_Collection));
		System.out.println(iol.classify(c_Collection));
	}
}
