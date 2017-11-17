package varargs;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TestVarargs {
	public static void aslist() {
		String[] arrayStr = { "marvin", "susan", "doudou" };
		List<String> listStr = Arrays.asList(arrayStr);
		System.out.println(listStr);
		List<String> listStr2 =Arrays.asList("ChinaSystem", "ZTE", "CDC", "Syniverse");
		System.out.println(listStr2);
		
		int[] arrayAge = { 31, 30, 1 };
		List<int[]> listI = Arrays.asList(arrayAge);
		System.out.println(listI);
		List<Integer> listII = Arrays.asList(1, 2, 3);
		System.out.println(listII);
		
	}

	
	public static void testFormat(String... args){
		MessageFormat mf = new MessageFormat("ni {0}, shi {1}", Locale.getDefault());
		String rst = mf.format(args);
		System.out.println(rst);
	}
	public static void main(String[] args) {
		testFormat("a", "b", "c");
	}
}
