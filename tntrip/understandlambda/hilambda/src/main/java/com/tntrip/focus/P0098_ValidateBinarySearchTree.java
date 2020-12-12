package com.tntrip.focus;


public class P0098_ValidateBinarySearchTree {

    public boolean isValidBST(TreeNode root) {
        if (root == null) {
            return false;
        }
        return doValid(Long.MIN_VALUE, root, Long.MAX_VALUE);
    }

    public boolean doValid(long low, TreeNode n, long high) {
        TreeNode left = n.left;
        TreeNode right = n.right;

        boolean valid = true;
        // go right
        if (right != null) {
            long newHigh = high;
            long newLow = n.val;
            valid = valid
                    && right.val > n.val && right.val < high
                    && doValid(newLow, right, newHigh);
        }

        // go left
        if (left != null) {
            long newHigh = n.val;
            long newLow = low;

            valid = valid
                    && left.val < n.val && left.val > low
                    && doValid(newLow, left, newHigh);
        }


        return valid;
    }


    public static void main(String[] args) {
        P0098_ValidateBinarySearchTree p = new P0098_ValidateBinarySearchTree();
        TreeNode root = new TreeNode(4);
        p.isValidBST(root);
    }
}

