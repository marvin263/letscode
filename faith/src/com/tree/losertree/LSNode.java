package com.tree.losertree;

import com.tree.showtree.VisibleNode;

public class LSNode implements VisibleNode {
    public LSNode L;
    public LSNode R;
    public int treeIndex;
    public int valueIndex;

    public static LSNode createNode(int treeIndex, int valueIndex) {
        LSNode node = new LSNode();
        node.treeIndex = treeIndex;
        node.valueIndex = valueIndex;
        return node;
    }

    @Override
    public VisibleNode left() {
        return L;
    }

    @Override
    public VisibleNode right() {
        return R;
    }

    @Override
    public String presentation() {
        return treeIndex + "," + valueIndex;
    }

    @Override
    public String toString() {
        return "LSNode [treeIndex=" + treeIndex + ", valueIndex=" + valueIndex
                + "]";
    }

}
