package com.tntrip.focus;


import java.util.ArrayList;
import java.util.List;

public class P0124_BinaryTreeMaximumPathSum {
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        List<TreeNode> cur = new ArrayList<>();
        cur.add(root);
        List<TreeNode> next = new ArrayList<>();
        return doMinDepth(0, cur, next);
    }

    public int doMinDepth(int prevHeight, List<TreeNode> cur, List<TreeNode> next) {
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
        return doMinDepth(prevHeight + 1, next, cur);
    }

}

