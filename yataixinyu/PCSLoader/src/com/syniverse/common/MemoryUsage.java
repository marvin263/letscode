package com.syniverse.common;

public class MemoryUsage {

	public static String human(long value) {
		String str = value + "";
		StringBuilder sb = new StringBuilder();
		int cnt = 0;
		for (int i = str.length() - 1; i >= 0; i--) {
			cnt++;
			if ((cnt == 3) && (i != 0)) {
				cnt = 0;
				sb.insert(0, "," + str.charAt(i));
			} else {
				sb.insert(0, str.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String memInfo() {
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		long free = Runtime.getRuntime().freeMemory();
		return "total=" + human(total) + " max=" + human(max) + " free="
				+ human(free);
	}

	public static void main(String[] args) {
		MemoryUsage mu = new MemoryUsage();
		System.out.println(human(Long.MAX_VALUE));
		System.out.println(human(Long.MIN_VALUE));
		System.out.println(human(Integer.MAX_VALUE));
		System.out.println(human(Integer.MIN_VALUE));
	}

}
