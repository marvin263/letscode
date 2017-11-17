import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 2016/01/01452056
 */
public class ConstructBinaryTree {
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

    public static class SelfChildren {
        TreeNode self;
        int lStartInclude;
        int lEndInclude;

        int rStartInclude;
        int rEndInclude;
        int directChildrenCount;
        int consumedChildrenCount;

        boolean skipIt() {
            return consumedChildrenCount == directChildrenCount;
        }
    }

    public TreeNode buildTree_pre(int[] preorder, int[] inorder) {
        if (inorder == null || inorder.length == 0) {
            return null;
        }

        TreeNode root = new TreeNode(preorder[0]);
        if (inorder.length == 1) {
            return root;
        }

        SelfChildren rootSC = new SelfChildren();
        rootSC.self = root;
        setupRootChildren(rootSC, inorder);

        List<SelfChildren> listSelfChildren = new ArrayList<>();
        listSelfChildren.add(rootSC);

        for (int i = 1; i < preorder.length; i++) {
            SelfChildren prevalueNode = new SelfChildren();
            prevalueNode.self = new TreeNode(preorder[i]);

            hangPrevalueToParent(prevalueNode, listSelfChildren, inorder);
        }

        return root;
    }

    private void hangPrevalueToParent(SelfChildren prevalueNode, List<SelfChildren> listSelfChildren, int[] inorder) {
        SelfChildren prevalueParent = null;
        boolean prevalueAtLeft = false;
        int posInInorder = -1;
        for (int i = listSelfChildren.size() - 1; i >= 0; i--) {
            SelfChildren upLevel = listSelfChildren.get(i);
            if (upLevel.skipIt()) {
                continue;
            }
            // prevalue is upLevel's left child
            if ((posInInorder = posInRange(prevalueNode.self.val, inorder, upLevel.lStartInclude, upLevel.lEndInclude)) != -1) {
                prevalueParent = upLevel;
                prevalueAtLeft = true;
                upLevel.self.left = prevalueNode.self;
                upLevel.consumedChildrenCount++;
                break;
            }
            // prevalue is upLevel's right child
            else if ((posInInorder = posInRange(prevalueNode.self.val, inorder, upLevel.rStartInclude, upLevel.rEndInclude)) != -1) {
                prevalueAtLeft = false;
                prevalueParent = upLevel;
                upLevel.self.right = prevalueNode.self;
                upLevel.consumedChildrenCount++;
                break;
            }
        }
        setupPrevalueChildren(prevalueNode, prevalueParent, prevalueAtLeft, posInInorder);

        addPrevalueNode2List(prevalueNode, listSelfChildren);
    }

    private void addPrevalueNode2List(SelfChildren prevalueNode, List<SelfChildren> listSelfChildren) {
        if (!prevalueNode.skipIt()) {
            listSelfChildren.add(prevalueNode);
        }
    }

    private void setupRootChildren(SelfChildren prevalueNode, int[] inorder) {
        int posInInorder = posInRange(prevalueNode.self.val, inorder, 0, inorder.length - 1);
        prevalueNode.lStartInclude = 0;
        prevalueNode.lEndInclude = posInInorder - 1;
        prevalueNode.rStartInclude = posInInorder + 1;
        prevalueNode.rEndInclude = inorder.length - 1;

        if (prevalueNode.lStartInclude <= prevalueNode.lEndInclude) {
            prevalueNode.directChildrenCount++;
        }
        if (prevalueNode.rStartInclude <= prevalueNode.rEndInclude) {
            prevalueNode.directChildrenCount++;
        }
    }

    private int posInRange(int prevalue, int[] inorder, int leftInclude, int rightInclude) {
        for (int i = leftInclude; i <= rightInclude; i++) {
            // Yes, we've found prevalue in the inorder sequence
            if (prevalue == inorder[i]) {
                return i;
            }
        }
        return -1;
    }

    private void setupPrevalueChildren(SelfChildren prevalueNode, SelfChildren prevalueParent,
                                       boolean prevalueAtLeft, int posInInorder) {
        if (prevalueAtLeft) {
            prevalueNode.lStartInclude = prevalueParent.lStartInclude;
            prevalueNode.lEndInclude = posInInorder - 1;
            prevalueNode.rStartInclude = posInInorder + 1;
            prevalueNode.rEndInclude = prevalueParent.lEndInclude;
        } else {
            prevalueNode.lStartInclude = prevalueParent.rStartInclude;
            prevalueNode.lEndInclude = posInInorder - 1;
            prevalueNode.rStartInclude = posInInorder + 1;
            prevalueNode.rEndInclude = prevalueParent.rEndInclude;
        }

        if (prevalueNode.lStartInclude <= prevalueNode.lEndInclude) {
            prevalueNode.directChildrenCount++;
        }
        if (prevalueNode.rStartInclude <= prevalueNode.rEndInclude) {
            prevalueNode.directChildrenCount++;
        }
    }

    public TreeNode buildTree_post(int[] inorder, int[] postorder) {
        if (inorder == null || inorder.length == 0) {
            return null;
        }

        TreeNode root = new TreeNode(postorder[postorder.length - 1]);
        if (inorder.length == 1) {
            return root;
        }

        SelfChildren rootSC = new SelfChildren();
        rootSC.self = root;
        setupRootChildren(rootSC, inorder);

        List<SelfChildren> listSelfChildren = new ArrayList<>();
        listSelfChildren.add(rootSC);

        for (int i = postorder.length - 2; i >= 0; i--) {
            SelfChildren prevalueNode = new SelfChildren();
            prevalueNode.self = new TreeNode(postorder[i]);

            hangPrevalueToParent(prevalueNode, listSelfChildren, inorder);
        }

        return root;
    }

    public static void main(String[] args) {
        ConstructBinaryTree cbt = new ConstructBinaryTree();

        int[] preorder = {1, 2, 3, 4, 5, 6, 7, 8};

        int[] inorder = {3, 4, 2, 5, 1, 7, 8, 6};

        int[] postorder = {4, 3, 5, 2, 8, 7, 6, 1};

        System.out.println(cbt.buildTree_pre(preorder, inorder));
        System.out.println(cbt.buildTree_post(inorder, postorder));

    }
}
