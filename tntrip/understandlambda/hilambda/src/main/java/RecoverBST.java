import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by nuc on 2015/12/30.
 */
public class RecoverBST {
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

    public static class RecoverNode {
        TreeNode parent;
        boolean selfAtLeft;
        TreeNode self;

        public RecoverNode(TreeNode parent, TreeNode self, boolean selfAtLeft) {
            this.parent = parent;
            this.self = self;
            this.selfAtLeft = selfAtLeft;
        }

        @Override
        public String toString() {
            return "RecoverNode{" +
                    "parent=" + parent +
                    ", selfAtLeft=" + selfAtLeft +
                    ", self=" + self +
                    '}';
        }
    }

    public static class NodeAndAs {
        TreeNode parent;
        TreeNode self;
        boolean selfAsLeft;

        public NodeAndAs(TreeNode self, boolean selfAsLeft, TreeNode parent) {
            this.parent = parent;
            this.self = self;
            this.selfAsLeft = selfAsLeft;
        }
    }

    private RecoverNode naa2rn(NodeAndAs naa) {
        RecoverNode rn = new RecoverNode(naa.parent, naa.self, naa.selfAsLeft);
        return rn;
    }

    public RecoverNode[] findMistakeByInorderTraversal(TreeNode root) {
        // Contains only 0 or 1 node
        if (root == null || (root.left == null && root.right == null)) {
            return new RecoverNode[0];
        }
        RecoverNode[] rnArrays = new RecoverNode[2];

        Deque<NodeAndAs> stack = new ArrayDeque<>();
        boolean firstEncounterOutoforder = true;
        RecoverNode lastRecover = null;
        NodeAndAs curNAA = new NodeAndAs(root, false, null);
        while (!(curNAA == null && stack.isEmpty())) {
            if (curNAA != null) {
                stack.push(curNAA);

                TreeNode ln = curNAA.self.left;
                curNAA = (ln == null) ? null : new NodeAndAs(ln, true, curNAA.self);

            } else {
                curNAA = stack.pop();
                // access node self

                TreeNode parent = stack.isEmpty() ? null : stack.peek().self;
                RecoverNode thisRecover = naa2rn(curNAA);

                if ((lastRecover != null) && (lastRecover.self.val > thisRecover.self.val)) {
                    // first time encounter outoforder
                    if (firstEncounterOutoforder) {
                        firstEncounterOutoforder = false;//just consumed first encounter outoforder
                        rnArrays[0] = lastRecover;
                        rnArrays[1] = thisRecover;
                    }
                    // second time encounter outoforder
                    else {
                        rnArrays[1] = thisRecover;
                        break;
                    }
                }

                lastRecover = thisRecover;
                TreeNode rn = curNAA.self.right;
                curNAA = (rn == null) ? null : new NodeAndAs(rn, false, curNAA.self);
            }
        }
        return rnArrays;
    }

    private TreeNode doExchange(RecoverNode[] rns, TreeNode root) {
        if (rns.length != 2) {
            return root;
        }

        RecoverNode rn0 = rns[0];
        RecoverNode rn1 = rns[1];

        TreeNode newRoot;
        if (root == rn0.self) {
            newRoot = rn1.self;
        } else if (root == rn1.self) {
            newRoot = rn0.self;
        } else {
            newRoot = root;
        }

        if (rn0.parent == rn1.self || rn1.parent == rn0.self) {
            exchangeAdjacent(rn0, rn1);
        } else {
            exchangeFarAway(rn0, rn1);
        }
        return newRoot;
    }

    private void exchangeFarAway(RecoverNode rn0, RecoverNode rn1) {
        TreeNode tn0 = rn0.self;
        TreeNode tn1 = rn1.self;
        TreeNode tmp = null;

        tmp = tn0.left;
        tn0.left = tn1.left;
        tn1.left = tmp;

        tmp = tn0.right;
        tn0.right = tn1.right;
        tn1.right = tmp;

        if (rn0.parent != null) {
            if (rn0.selfAtLeft) {
                rn0.parent.left = rn1.self;
            } else {
                rn0.parent.right = rn1.self;
            }
        }
        if (rn1.parent != null) {
            if (rn1.selfAtLeft) {
                rn1.parent.left = rn0.self;
            } else {
                rn1.parent.right = rn0.self;
            }
        }
    }


    private void exchangeAdjacent(RecoverNode rn0, RecoverNode rn1) {

        TreeNode rn0_R = rn0.self.right;
        TreeNode rn0_L = rn0.self.left;

        TreeNode rn1_R = rn1.self.right;
        TreeNode rn1_L = rn1.self.left;

        if (rn0.parent == rn1.self) {
            RecoverNode rn_high = rn1;
            RecoverNode rn_low = rn0;
            TreeNode rn_low_R = rn0_R;
            TreeNode rn_low_L = rn0_L;
            doExchangeAdjacent(rn_high, rn_low, rn_low_R, rn_low_L);
        } else if (rn1.parent == rn0.self) {
            RecoverNode rn_high = rn0;
            RecoverNode rn_low = rn1;
            TreeNode rn_low_R = rn1_R;
            TreeNode rn_low_L = rn1_L;
            doExchangeAdjacent(rn_high, rn_low, rn_low_R, rn_low_L);
        }
    }

    private void doExchangeAdjacent(RecoverNode rn_high, RecoverNode rn_low,
                                    TreeNode rn_low_R, TreeNode rn_low_L) {

        //change rn0.self parent
        if (rn_high.parent != null) {
            if (rn_high.selfAtLeft) {
                rn_high.parent.left = rn_low.self;
            } else {
                rn_high.parent.right = rn_low.self;
            }
        }
        // change rn0.self children
        if (rn_low.selfAtLeft) {
            rn_low.self.left = rn_high.self;
            rn_low.self.right = rn_high.self.right;
        } else {
            rn_low.self.right = rn_high.self;
            rn_low.self.left = rn_high.self.left;
        }


        // change rn1.self parent
        if (rn_low.selfAtLeft) {
            rn_low.self.left = rn_high.self;
        } else {
            rn_low.self.right = rn_high.self;
        }
        // change rn1.self children
        rn_high.self.right = rn_low_R;
        rn_high.self.left = rn_low_L;
    }

    public TreeNode recoverTree(TreeNode root) {
        RecoverNode[] rns = findMistakeByInorderTraversal(root);
        if (rns.length != 2) {
            return null;
        }

        return doExchange(rns, root);
    }

    public static void main(String[] args) {
        RecoverBST rbst = new RecoverBST();
        TreeNode tn0 = new TreeNode(0);
        TreeNode tn1 = new TreeNode(1);

        tn0.left = tn1;

        TreeNode root = tn0;
        System.out.println(root);

        TreeNode newRoot = rbst.recoverTree(root);

        System.out.println(newRoot);

    }
}
