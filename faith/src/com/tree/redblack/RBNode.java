package com.tree.redblack;

import java.awt.Color;

import com.tree.showtree.VisibleColorNode;
import com.tree.showtree.VisibleNode;

public class RBNode implements VisibleColorNode {
    public int data;
    public RBNode L;
    public RBNode R;
    public Color color;

    public static RBNode createRedNode(int data) {
        return createNode(data, Color.red);
    }

    public static RBNode createBlackNode(int data) {
        return createNode(data, Color.black);
    }

    public static RBNode copyNode(RBNode orgn) {
        return RBNode.createNode(orgn.data, orgn.color);
    }

    private static RBNode createNode(int data, Color color) {
        RBNode n = new RBNode();
        n.data = data;
        n.color = color;
        return n;
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
        return data + "";
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public String toString() {
        return "RBNode [data=" + data + ", color=" + color + "]";
    }
}
