package com.tree.redblack;

import com.tree.avl.AVLNode;
import com.tree.avl.AVLUtil;
import com.tree.showtree.AbstractShowTreeFrame;
import com.tree.showtree.VisibleNode;

public class RedBlack_ShowTreeFrame extends AbstractShowTreeFrame {
	private static final long serialVersionUID = 1L;

	public RedBlack_ShowTreeFrame(VisibleNode root) {
		super(root);
	}

	@Override
	public void insertNode(int newValue) {
		// previousRoot = AVLUtil.copyAVLtree((AVLNode) currentRoot);
		//
		// AVLUtil.InsertResult ir = AVLUtil.insert((AVLNode) currentRoot,
		// newValue);
		// currentRoot = ir.newRoot;
	}

	@Override
	public void deleteNode(VisibleNode nodeToBeDeleted) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		int[] array = new int[] { 10, 20, 30, 30, 10, 40, 50 };

		AVLNode root = AVLUtil.createAVLtree(array);

		RedBlack_ShowTreeFrame frm = new RedBlack_ShowTreeFrame(root);

		AbstractShowTreeFrame.showTreeFrm(frm, "Red Black Tree");
	}

}
