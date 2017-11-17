package effective2.Chapter7.Item41;

// What does this program print? - Page 194

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SetList {
	public static void main(String[] args) {
		Set<Integer> set = new TreeSet<Integer>();
		List<Integer> list = new ArrayList<Integer>();
		// [-3, -2, -1, 0, 1, 2]
		for (int i = -3; i < 3; i++) {
			set.add(i);
			list.add(i);
		}

		for (int i = 0; i < 3; i++) {
			set.remove(i);// Set 只有 remove(oneElement)。所以，auto-box后，删除。一切 OK

			// List 则 remove(index) 和 remove(oneElement)
			list.remove(i);// Marvin： 实际走的是 remove(index)
							// 而不是 auto-box 后，进行 remove(oneElement)
		}

		System.out.println(set + " " + list);
	}
}
