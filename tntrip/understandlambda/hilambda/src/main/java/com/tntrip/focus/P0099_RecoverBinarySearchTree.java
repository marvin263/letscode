package com.tntrip.focus;


public class P0099_RecoverBinarySearchTree {
    public static TreeNode[] EMPTY = new TreeNode[0];

    public static class BoundNode extends TreeNode {

    }


    public void recoverTree(TreeNode root) {
        TreeNode[] nodes = findNodes(new BoundNode(), root, new BoundNode());
        if (nodes != EMPTY) {
            int tmp = nodes[0].val;
            nodes[0].val = nodes[1].val;
            nodes[1].val = tmp;
        }
    }

    private TreeNode[] findNodes(TreeNode high, TreeNode n, TreeNode low) {
        TreeNode left = n.left;
        TreeNode right = n.right;
        TreeNode goRightNewHigh = high;
        TreeNode goRightNewLow = low;
        TreeNode goLeftNewHigh = high;
        TreeNode goLeftNewLow = low;
        // go right
        if (right != null) {
            if (!(right.val > n.val)) {
                return new TreeNode[]{right, n};
            }
            if (!(high instanceof BoundNode) && !(right.val < high.val)) {
                return new TreeNode[]{right, high};
            }
            goRightNewHigh = high;
            goRightNewLow = n;
        }
        // go left
        if (left != null) {
            if (!(left.val < n.val)) {
                return new TreeNode[]{left, n};
            }
            if (!(low instanceof BoundNode) && !(left.val > low.val)) {
                return new TreeNode[]{left, low};
            }
            goLeftNewHigh = n;
            goLeftNewLow = low;
        }

        if (right != null) {
            TreeNode[] dddd = findNodes(goRightNewHigh, right, goRightNewLow);
            if (dddd != EMPTY) {
                return dddd;
            }
        }
        if (left != null) {
            TreeNode[] dddd = findNodes(goLeftNewHigh, left, goLeftNewHigh);
            if (dddd != EMPTY) {
                return dddd;
            }
        }
        return EMPTY;
    }

    public static void main(String[] args) {
        //[3,1,4,null,null,2]
        P0099_RecoverBinarySearchTree p = new P0099_RecoverBinarySearchTree();
        TreeNode n3 = new TreeNode(3);
        TreeNode n1 = new TreeNode(1);
        TreeNode n4 = new TreeNode(4);
        TreeNode n2 = new TreeNode(2);
        n3.left = n1;
        n3.right = n4;
        
        n4.left = n2;
        p.recoverTree(n3);
    }
}

