package com.tree.commontree;
import com.tree.showtree.VisibleNode;

public class Node implements VisibleNode {
    public int data;
    public Node L;
    public Node R;

    public static Node createNode(int data) {
        Node n = new Node();
        n.data = data;
        return n;
    }

    public static Node copyNode(Node orgn) {
        return Node.createNode(orgn.data);
    }

    public VisibleNode left() {
        return L;
    }

    public VisibleNode right() {
        return R;
    }

    public String presentation() {
        return data + "";
    }

    @Override
    public String toString() {
        return "data=" + data;
    }
}
