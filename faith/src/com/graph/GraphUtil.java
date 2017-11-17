package com.graph;

import com.util.MultiDimensionArray;

public class GraphUtil {
	public static String[][] buildAdjacencyMatrix(String fullPath) {
		return MultiDimensionArray.construct_2D_Array(fullPath);
	}

	/**
	 * "1"-->true--connected
	 * 
	 * "0"-->false--NOT connected
	 * 
	 * @param str
	 * @return
	 */
	private static boolean str2Connected(String str) {
		if ("1".equals(str)) {
			return true;
		}
		return false;
	}

	public static void Depth_First_Search(String[][] am) {
		int vCount = am.length;

	}

	public static void Breadth_First_Search(String[][] am) {

	}

	public static void fdfd() {

	}
}
