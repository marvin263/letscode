package com.tree.showtree;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.tree.commontree.TreeUtil;

public class ShowStaticTreeFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public ShowStaticTreeFrame(VisibleNode root) {
        initComponent(root);
    }

    private void initComponent(VisibleNode root) {
        ShowTreeComponent currentTreeComp = new ShowTreeComponent(65, 40, root, 20, 30);
        this.getContentPane().add(currentTreeComp, BorderLayout.CENTER);
    }

    public static void showTreeFrm(VisibleNode root, String frmTitle) {
        ShowStaticTreeFrame frm = new ShowStaticTreeFrame(root);
        frm.setSize(700, 500);
        frm.setTitle(frmTitle);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

    public static void main(String[] args) {
        showTreeFrm(TreeUtil.createFullTree(4), "Full Binary Tree");
    }
}
