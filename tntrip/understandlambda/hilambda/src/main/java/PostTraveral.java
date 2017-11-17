import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by libing2 on 2015/12/29.
 */
public class PostTraveral {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public static class NodeAndRightAccessed {
        public TreeNode node;
        public boolean rightChildAccessed;

        public NodeAndRightAccessed(TreeNode node, boolean rightChildAccessed) {
            this.node = node;
            this.rightChildAccessed = rightChildAccessed;
        }
    }

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        if (root == null) {
            return rst;
        }

        Deque<NodeAndRightAccessed> stack = new ArrayDeque<>();
        NodeAndRightAccessed cur = new NodeAndRightAccessed(root, false);
        while (!(cur == null && stack.isEmpty())) {
            if (cur == null) {
                cur = stack.peek();
                if (cur.rightChildAccessed) {
                    cur = stack.pop();
                    rst.add(cur.node.val);
                    cur = null;
                } else {
                    cur.rightChildAccessed = true;
                    if (cur.node.right == null) {
                        cur = null;
                    } else {
                        cur = new NodeAndRightAccessed(cur.node.right, false);
                    }
                }
            } else {
                if (cur.node.left != null) {
                    stack.push(new NodeAndRightAccessed(cur.node, false));
                    cur = new NodeAndRightAccessed(cur.node.left, false);
                } else if (cur.node.right != null) {
                    stack.push(new NodeAndRightAccessed(cur.node, true));
                    cur = new NodeAndRightAccessed(cur.node.right, false);
                }
                // both left&&right are null
                else {
                    rst.add(cur.node.val);
                    cur = null;
                }
            }
        }
        return rst;
    }

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();

        TreeNode cur = root;
        while (!(cur == null && stack.isEmpty())) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                rst.add(cur.val);
                cur = cur.right;
            }
        }
        return rst;
    }

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();

        TreeNode cur = root;
        while (!(cur == null && stack.isEmpty())) {
            if (cur != null) {
                rst.add(cur.val);
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                cur = cur.right;
            }
        }
        return rst;
    }

    public List<Integer> preorderTraversal_2(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        recursivePreOrderTraversal(rst, root);
        return rst;
    }

    public void recursivePreOrderTraversal(List<Integer> rst, TreeNode root) {
        if (root == null) {
            return;
        }
        rst.add(root.val);
        recursivePreOrderTraversal(rst, root.left);
        recursivePreOrderTraversal(rst, root.right);
    }


    public List<Integer> inorderTraversal_2(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        recursiveInOrderTraversal(rst, root);
        return rst;
    }

    public void recursiveInOrderTraversal(List<Integer> rst, TreeNode root) {
        if (root == null) {
            return;
        }
        recursiveInOrderTraversal(rst, root.left);
        rst.add(root.val);
        recursiveInOrderTraversal(rst, root.right);
    }

    public List<Integer> postorderTraversal_2(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        recursivePostOrderTraversal(rst, root);
        return rst;
    }

    public void recursivePostOrderTraversal(List<Integer> rst, TreeNode root) {
        if (root == null) {
            return;
        }
        recursivePostOrderTraversal(rst, root.left);
        recursivePostOrderTraversal(rst, root.right);
        rst.add(root.val);
    }
}
