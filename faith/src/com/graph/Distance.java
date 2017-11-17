package com.graph;

public class Distance implements Weight {
	public final int v;
	public final boolean NA;

	public Distance(int v, boolean NA) {
		this.v = v;
		this.NA = NA;

	}

	@Override
	public int v() {
		return v;
	}

	@Override
	public boolean NA() {
		return NA;
	}

	@Override
	public String toString() {
		return "Distance [v=" + v + ", NA=" + NA + "]";
	}
}
