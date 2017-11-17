import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by nuc on 2015/12/30.
 */
public class RecoverBST2 {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        @Override
        public String toString() {
            return "TreeNode{" +
                    "val=" + val +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

    public TreeNode[] findMistakeByInorderTraversal(TreeNode root) {
        // Contains only 0 or 1 node
        if (root == null || (root.left == null && root.right == null)) {
            return new TreeNode[0];
        }
        TreeNode[] tnArray = new TreeNode[2];

        Deque<TreeNode> stack = new ArrayDeque<>();
        boolean firstEncounterOutoforder = true;
        TreeNode last = null;
        TreeNode cur = root;

        while (!(cur == null && stack.isEmpty())) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;

            } else {
                cur = stack.pop();
                // access node self
                if ((last != null) && (last.val > cur.val)) {
                    // first time encounter outoforder
                    if (firstEncounterOutoforder) {
                        firstEncounterOutoforder = false;//just consumed first encounter outoforder
                        tnArray[0] = last;
                        tnArray[1] = cur;
                    }
                    // second time encounter outoforder
                    else {
                        tnArray[1] = cur;
                        break;
                    }
                }
                last = cur;
                cur = cur.right;
            }
        }
        return tnArray;
    }
    
    private void doExchange(TreeNode[] rns) {
        if (rns.length != 2) {
            return;
        }
        TreeNode rn0 = rns[0];
        TreeNode rn1 = rns[1];

        int tmp = rn0.val;
        rn0.val = rn1.val;
        rn1.val = tmp;
    }

    public void recoverTree(TreeNode root) {
        TreeNode[] rns = findMistakeByInorderTraversal(root);
        if (rns.length != 2) {
            return;
        }
        doExchange(rns);
    }

    public static void main(String[] args) {
        RecoverBST2 rbst = new RecoverBST2();
        TreeNode tn0 = new TreeNode(0);
        TreeNode tn1 = new TreeNode(1);

        tn0.left = tn1;

        System.out.println(tn0);

        rbst.recoverTree(tn0);

        System.out.println(tn0);
    }
}
