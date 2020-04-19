package com.tree.bst;

import com.tree.commontree.Node;

public class BSTUtil {
    public static Node createBSTtree(int[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        Node root = Node.createNode(array[0]);
        for (int i = 1; i < array.length; i++) {
            int nowData = array[i];
            Node parent = null;
            Node tmpNode = root;
            boolean asRightChild = true;
            while (tmpNode != null) {
                parent = tmpNode;
                // to be inserted data is bigger
                if (nowData >= tmpNode.data) {
                    tmpNode = tmpNode.R;
                    asRightChild = true;
                } else {
                    tmpNode = tmpNode.L;
                    asRightChild = false;
                }
            }
            // since here, n == null
            if (asRightChild) {
                parent.R = Node.createNode(nowData);
            } else {
                parent.L = Node.createNode(nowData);
            }
        }
        return root;
    }

    public static Node copyBSTtree(Node root) {
        if (root == null) {
            return null;
        }
        Node left = copyBSTtree(root.L);
        Node right = copyBSTtree(root.R);
        Node newRoot = Node.copyNode(root);
        newRoot.L = left;
        newRoot.R = right;
        return newRoot;
    }

    public static Node insertIntoBST(Node root, int data) {
        if (root == null) {
            return Node.createNode(data);
        }

        boolean asRightChild = false;

        Node parent = null;
        Node tmp = root;
        while (tmp != null) {
            parent = tmp;
            if (data < tmp.data) {
                tmp = tmp.L;
                asRightChild = false;
            } else {
                tmp = tmp.R;
                asRightChild = true;
            }
        }

        if (asRightChild) {
            parent.R = Node.createNode(data);
        } else {
            parent.L = Node.createNode(data);
        }
        return root;
    }

    /**
     * We suppose we've found the node on the tree. Now, delete this found node
     * <code>nodeToBeDeleted</code>
     * 
     * @param root
     * @param nodeToBeDeleted
     * @return
     */
    public static Node deleteFromBST(Node root, Node nodeToBeDeleted) {
        Node parent = findParent(root, nodeToBeDeleted);
        // nodeToBeDeleted is root
        if (parent == null) {
            return null;
        }
        // nodeToBeDeleted is a leaf
        if (nodeToBeDeleted.L == null && nodeToBeDeleted.R == null) {
            if (parent.L == nodeToBeDeleted) {
                parent.L = null;
            } else {
                parent.R = null;
            }
            return root;
        }
        // nodeToBeDeleted.L is null (R is NOT null)
        if (nodeToBeDeleted.L == null) {
            if (parent.L == nodeToBeDeleted) {
                parent.L = nodeToBeDeleted.R;
            } else {
                parent.R = nodeToBeDeleted.R;
            }
            return root;
        }
        // nodeToBeDeleted.R is null (L is NOT null)
        if (nodeToBeDeleted.R == null) {
            if (parent.L == nodeToBeDeleted) {
                parent.L = nodeToBeDeleted.L;
            } else {
                parent.R = nodeToBeDeleted.L;
            }
            return root;
        }
        // neither nodeToBeDeleted.L nor nodeToBeDeleted.R is null
        Node[] arraySuccessor = findSuccessor(nodeToBeDeleted);

        Node successorParent = arraySuccessor[0];
        Node successor = arraySuccessor[1];

        if (successorParent == nodeToBeDeleted) {
            if (parent.R == nodeToBeDeleted) {
                parent.R = successor;
                successor.L = nodeToBeDeleted.L;
            } else {
                parent.L = successor;
                successor.L = nodeToBeDeleted.L;
            }
        } else {
            if (parent.R == nodeToBeDeleted) {
                parent.R = successor;
                successor.L = nodeToBeDeleted.L;
                successorParent.L = successor.R;
            } else {
                parent.L = successor;
                successor.L = nodeToBeDeleted.L;
                successorParent.L = successor.R;
            }
        }

        return root;
    }

    public static Node findParent(Node root, Node nodeToBeDeleted) {
        Node parent = null;
        Node tmp = root;
        while (tmp != nodeToBeDeleted) {
            parent = tmp;
            if (tmp.data < nodeToBeDeleted.data) {
                tmp = tmp.R;
            } else {
                tmp = tmp.L;
            }
        }
        return parent;
    }

    public static Node[] findSuccessor(Node nodeToBeDeleted) {
        // No right child at all
        if (nodeToBeDeleted.R == null) {
            return null;
        }
        Node successorParent = null;
        Node successor = nodeToBeDeleted;
        Node tmp = nodeToBeDeleted.R;
        // when tmp is null, successor is ok, and we also find successorParent
        while (tmp != null) {
            successorParent = successor;
            successor = tmp;
            tmp = tmp.L;
        }
        return new Node[] { successorParent, successor };
    }
}
