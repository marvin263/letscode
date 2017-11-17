package com.tree.avl;

import com.tree.showtree.AbstractShowTreeFrame;
import com.tree.showtree.VisibleNode;

public class AVL_ShowTreeFrame extends AbstractShowTreeFrame {
	private static final long serialVersionUID = 1L;

	public AVL_ShowTreeFrame(VisibleNode root) {
		super(root);
	}

	@Override
	public void insertNode(int newValue) {
		previousRoot = AVLUtil.copyAVLtree((AVLNode) currentRoot);

		AVLUtil.InsertResult ir = AVLUtil.insert((AVLNode) currentRoot,
				newValue);
		currentRoot = ir.newRoot;
	}

	@Override
	public void deleteNode(VisibleNode nodeToBeDeleted) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		int[] array = new int[] { 10, 20, 30, 30, 10, 40, 50 };

		AVLNode root = AVLUtil.createAVLtree(array);

		AVL_ShowTreeFrame frm = new AVL_ShowTreeFrame(root);

		AbstractShowTreeFrame.showTreeFrm(frm,
				"AVL--Self-Balancing/Height-Balanced Binary Search Tree");
	}

}
