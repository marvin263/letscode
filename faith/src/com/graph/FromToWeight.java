package com.graph;

import java.util.ArrayList;
import java.util.List;

public class FromToWeight {
	public final int from;
	public final List<Integer> to = new ArrayList<Integer>();
	public final Weight w;

	public FromToWeight(int from, Weight w) {
		this.from = from;
		this.w = w;
	}

}
