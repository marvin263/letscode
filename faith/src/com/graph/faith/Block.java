package com.graph.faith;

import java.util.ArrayList;
import java.util.List;

public class Block {
	public List<VisibleGNode> listNode = new ArrayList<VisibleGNode>();

	public int calcBlockWidth() {
		int nodeCount = listNode.size();
		return nodeCount == 0 ? 0
				: (Level.ref_plus_node_width * nodeCount + Level.ref_width);
	}
}
