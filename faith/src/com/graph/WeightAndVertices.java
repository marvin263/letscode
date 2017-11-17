package com.graph;

import java.util.ArrayList;
import java.util.List;

public class WeightAndVertices {
	public final Weight w;
	public List<Integer> vertices = new ArrayList<Integer>();

	private WeightAndVertices(final Weight w) {
		this.w = w;
	}

	public static WeightAndVertices createWeightAndVertices(final Weight w) {
		WeightAndVertices wav = new WeightAndVertices(w);
		return wav;
	}

	@Override
	public String toString() {
		return "WeightAndVertices [w=" + w + ", vertices=" + vertices + "]";
	}
}
