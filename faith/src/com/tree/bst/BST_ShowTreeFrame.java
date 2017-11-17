package com.tree.bst;

import com.tree.commontree.Node;
import com.tree.showtree.AbstractShowTreeFrame;
import com.tree.showtree.VisibleNode;

public class BST_ShowTreeFrame extends AbstractShowTreeFrame {
	private static final long serialVersionUID = 1L;

	public BST_ShowTreeFrame(VisibleNode root) {
		super(root);
	}

	@Override
	public void insertNode(int newValue) {
		previousRoot = BSTUtil.copyBSTtree((Node) currentRoot);

		currentRoot = BSTUtil.insertIntoBST((Node) currentRoot, newValue);
	}

	@Override
	public void deleteNode(VisibleNode nodeToBeDeleted) {
		previousRoot = BSTUtil.copyBSTtree((Node) currentRoot);

		currentRoot = BSTUtil.deleteFromBST((Node) currentRoot,
				(Node) nodeToBeDeleted);

	}

	public static void main(String[] args) {
		int[] array = new int[] { 40, 20, 30, 50, 30, 10, 60 };

		Node root = BSTUtil.createBSTtree(array);

		BST_ShowTreeFrame frm = new BST_ShowTreeFrame(root);

		AbstractShowTreeFrame.showTreeFrm(frm, "BST--Binary Search Tree");
	}
}
