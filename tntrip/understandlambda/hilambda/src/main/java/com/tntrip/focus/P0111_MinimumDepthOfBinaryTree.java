package com.tntrip.focus;


import java.util.ArrayList;
import java.util.List;

public class P0111_MinimumDepthOfBinaryTree {
    public int maxPathSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        List<TreeNode> cur = new ArrayList<>();
        cur.add(root);
        List<TreeNode> next = new ArrayList<>();
        return maxPathSum(0, cur, next);
    }

    public int maxPathSum(int prevHeight, List<TreeNode> cur, List<TreeNode> next) {
        next.clear();
        for (TreeNode n : cur) {
            if (n.left == null && n.right == null) {
                return prevHeight + 1;
            }
            if (n.left != null) {
                next.add(n.left);
            }
            if (n.right != null) {
                next.add(n.right);
            }
        }
        return maxPathSum(prevHeight + 1, next, cur);
    }

}

