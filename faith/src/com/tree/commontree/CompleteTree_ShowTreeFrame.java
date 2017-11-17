package com.tree.commontree;

import com.tree.showtree.AbstractShowTreeFrame;
import com.tree.showtree.VisibleNode;
import com.util.CommUtil;

public class CompleteTree_ShowTreeFrame extends AbstractShowTreeFrame {
	private static final long serialVersionUID = 1L;

	public CompleteTree_ShowTreeFrame(VisibleNode root) {
		super(root);
	}

	@Override
	public void insertNode(int newValue) {

	}

	@Override
	public void deleteNode(VisibleNode nodeToBeDeleted) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		Node root = TreeUtil.createCompleteTree(CommUtil.genereateRandomArray(
				13, 51));

		CompleteTree_ShowTreeFrame frm = new CompleteTree_ShowTreeFrame(root);

		AbstractShowTreeFrame.showTreeFrm(frm, "完全二叉树--Complete Binary Tree");
	}

}
