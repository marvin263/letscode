package com.tree.commontree;

import com.tree.showtree.AbstractShowTreeFrame;
import com.tree.showtree.VisibleNode;

public class FullTree_ShowTreeFrame extends AbstractShowTreeFrame {
    private static final long serialVersionUID = 1L;

    public FullTree_ShowTreeFrame(VisibleNode root) {
        super(root);
    }

    @Override
    public void insertNode(int newValue) {

    }

    @Override
    public void deleteNode(VisibleNode nodeToBeDeleted) {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args) {
        Node root = TreeUtil.createFullTree(6);

        FullTree_ShowTreeFrame frm = new FullTree_ShowTreeFrame(root);

        AbstractShowTreeFrame.showTreeFrm(frm, "满二叉树--Full Binary Tree");
    }

}
