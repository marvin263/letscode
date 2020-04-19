package com.tree.avl;
import com.tree.showtree.VisibleNode;

public class AVLNode implements VisibleNode {
    public AVLNode L;
    public AVLNode R;
    public int data;
    public BalanceFactor bf;

    public static AVLNode creatAVLNode(int data) {
        AVLNode n = new AVLNode();
        n.data = data;
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

    static enum BalanceFactor {
        LH, // left higher
        RH, // right higher
        EH; // equal higher
    }

    public static AVLNode copyAVLNode(AVLNode orgn) {
        AVLNode copy = AVLNode.creatAVLNode(orgn.data);
        copy.bf = orgn.bf;
        return copy;
    }

    @Override
    public String toString() {
        return "data=" + data + ", bf=" + bf;
    }

    @Override
    public String presentation() {
        return data + "";
    }
}
