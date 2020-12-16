package com.tntrip.focus;


public class P0226_InvertBinaryTree {

    public TreeNode invertTree(TreeNode root) {
        doInvert(root);
        return root;
    }


    private void doInvert(TreeNode n) {
        if (n == null) {
            return;
        }
        TreeNode left = n.left;
        TreeNode right = n.right;

        doInvert(left);
        doInvert(right);
        n.left = right;
        n.right = left;
    }

    public static void main(String[] args) {
        P0226_InvertBinaryTree p = new P0226_InvertBinaryTree();
        TreeNode root = new TreeNode(4);
        TreeNode t2 = new TreeNode(2);
        TreeNode t7 = new TreeNode(7);
        TreeNode t1 = new TreeNode(1);
        TreeNode t3 = new TreeNode(3);
        TreeNode t6 = new TreeNode(6);
        TreeNode t9 = new TreeNode(9);

        root.left = t2;
        root.right = t7;
        t2.left = t1;
        t2.right = t3;
        t7.left = t6;
        t7.right = t9;
        p.invertTree(root);
    }
}

