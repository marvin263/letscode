import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class afdf {
	public static List<String> findSuccesiveSameChar(String str) {
		if (str == null || str.length() == 0) {
			return Collections.emptyList();
		}

		List<String> list = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		char last = str.charAt(0);
		sb.append(last);
		char cur = (char) 19;
		for (int i = 1; i < str.length(); i++) {
			cur = str.charAt(i);
			if (last == cur) {
				sb.append(cur);
			} else {
				// OK, find one
				list.add(sb.toString());
				sb.delete(0, sb.length());
				sb.append(cur);
			}
			last = cur;
		}
		list.add(sb.toString());
		System.out.println("original string:" + str + ", result=" + list);
		return list;
	}

	public static void main(String[] args) {
		findSuccesiveSameChar("abbccdddfsdeeeeef");
	}
}
