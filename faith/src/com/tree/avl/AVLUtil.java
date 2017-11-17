package com.tree.avl;

public class AVLUtil {

	public static AVLNode createAVLtree(int[] array) {
		AVLNode root = null;
		for (int i = 0; i < array.length; i++) {
			InsertResult ir = insert(root, array[i]);
			root = ir.newRoot;

		}
		return root;
	}

	public static AVLNode copyAVLtree(AVLNode root) {
		if (root == null) {
			return null;
		}
		AVLNode left = copyAVLtree(root.L);
		AVLNode right = copyAVLtree(root.R);

		AVLNode newRoot = AVLNode.copyAVLNode(root);

		newRoot.L = left;
		newRoot.R = right;

		return newRoot;
	}

	/**
	 * <pre>
	 * 			O1							O2
	 * 		O2				-->			O3		O1
	 * 
	 * 	O3
	 * </pre>
	 * 
	 * naming convenience: n's level 1, son's level 2, grandson's level 3
	 * 
	 * @param n_1
	 */
	public static AVLNode R_Rotate(AVLNode n_1) {
		AVLNode l_2 = n_1.L;
		AVLNode lr_3 = null;
		if (l_2 != null) {
			lr_3 = l_2.R;
		}
		l_2.R = n_1;
		n_1.L = lr_3;
		return l_2;
	}

	/**
	 * <pre>
	 * 	O1										O2
	 * 		O2				-->				O1		O3
	 * 
	 * 			O3
	 * </pre>
	 * 
	 * naming convenience: n's level 1, son's level 2, grandson's level 3
	 * 
	 * @param n_1
	 */
	public static AVLNode L_Rotate(AVLNode n_1) {
		AVLNode r_2 = n_1.R;
		AVLNode rl_3 = null;
		if (r_2 != null) {
			rl_3 = r_2.L;
		}

		r_2.L = n_1;
		n_1.R = rl_3;
		return r_2;
	}

	/**
	 * <b>Now we've done appending leaf</b>
	 * <p>
	 * 
	 * <b>The tree whose root is l_2 JUST grows taller due to appending a leaf
	 * to this tree</b>
	 * 
	 * <p>
	 * <b>n_1 currently is LH</b>
	 * 
	 * @param n_1
	 */
	public static AVLNode leftBalance(AVLNode n_1) {
		AVLNode l_2 = n_1.L;
		switch (l_2.bf) {
		/*
		 * Think about why this is impossible? If it were currently EH, it could
		 * not have grown taller.
		 * 
		 * Currently, it could only be LH or RH
		 */
		case EH:
			throw new RuntimeException(
					"l_2 has just grown taller. If it were currently EH, it could not have grown taller");

			/**
			 * new leaf just made l_2 LH (new leaf had been appended on l_2's
			 * left)
			 * 
			 * 1. n_1 is LH
			 * 
			 * 2. n_1's left subtree grows taller
			 * 
			 * 3. l_2 is now LH
			 * 
			 * <pre>
			 * 			01				02
			 * 		02		--> 	03		01
			 * 	03
			 * </pre>
			 */
		case LH:
			n_1.bf = AVLNode.BalanceFactor.EH;
			l_2.bf = AVLNode.BalanceFactor.EH;
			return R_Rotate(n_1);

			/**
			 * new leaf just made l_2 RH (new leaf had been appended on l_2's
			 * left)
			 * 
			 * 1. n_1 is LH
			 * 
			 * 2. n_1's left subtree grows taller
			 * 
			 * 3. l_2 is now LH
			 * 
			 * <pre>
			 * 			01					01				03
			 * 		02		--> 		03		-->		02		01
			 * 			03			02
			 * </pre>
			 */
		case RH:
			AVLNode lr_3 = l_2.R;
			switch (lr_3.bf) {
			case LH:
				n_1.bf = AVLNode.BalanceFactor.RH;
				l_2.bf = AVLNode.BalanceFactor.EH;
				break;
			case EH:
				n_1.bf = AVLNode.BalanceFactor.EH;
				l_2.bf = AVLNode.BalanceFactor.EH;
				break;
			case RH:
				n_1.bf = AVLNode.BalanceFactor.EH;
				l_2.bf = AVLNode.BalanceFactor.LH;
				break;
			}
			lr_3.bf = AVLNode.BalanceFactor.EH;
			AVLNode newRoot = L_Rotate(l_2);
			n_1.L = newRoot;
			return R_Rotate(n_1);
		}
		throw new RuntimeException("Never reach here");
	}

	/**
	 * See leftBanlance
	 * 
	 * 
	 * @param n_1
	 */
	public static AVLNode rightBalance(AVLNode n_1) {
		AVLNode r_2 = n_1.R;
		switch (r_2.bf) {
		case EH:
			throw new RuntimeException(
					"r_2 has just grown taller. If it were currently EH, it could not have grown taller");
		case RH:
			n_1.bf = AVLNode.BalanceFactor.EH;
			r_2.bf = AVLNode.BalanceFactor.EH;
			return L_Rotate(n_1);
		case LH:
			AVLNode rl_3 = r_2.L;
			switch (rl_3.bf) {
			case RH:
				n_1.bf = AVLNode.BalanceFactor.LH;
				r_2.bf = AVLNode.BalanceFactor.EH;
				break;
			case EH:
				n_1.bf = AVLNode.BalanceFactor.EH;
				r_2.bf = AVLNode.BalanceFactor.EH;
				break;
			case LH:
				n_1.bf = AVLNode.BalanceFactor.EH;
				r_2.bf = AVLNode.BalanceFactor.RH;
				break;
			}
			rl_3.bf = AVLNode.BalanceFactor.EH;
			AVLNode newRoot = R_Rotate(r_2);
			n_1.R = newRoot;
			return L_Rotate(n_1);
		}
		throw new RuntimeException("Never reach here");
	}

	static class InsertResult {
		public AVLNode newRoot;
		public boolean bGrowTaller;

		public InsertResult(AVLNode newRoot, boolean bGrowTaller) {
			this.newRoot = newRoot;
			this.bGrowTaller = bGrowTaller;
		}
	}

	/**
	 * @param node
	 * @param newValue
	 * @return
	 */
	public static InsertResult insert(AVLNode node, int newValue) {
		// do insertion
		if (node == null) {
			AVLNode createdNewNode = new AVLNode();
			createdNewNode.data = newValue;
			createdNewNode.L = createdNewNode.R = null;
			createdNewNode.bf = AVLNode.BalanceFactor.EH;
			// Yes, tree grows taller
			return new InsertResult(createdNewNode, true);
		}
		// find node, and do insertion
		else {
			// less. newValue should be on the left
			if (newValue < node.data) {
				// Whether node.L grows taller?
				InsertResult ir = insert(node.L, newValue);
				// IMPORTANT. After insertion, root node may change
				node.L = ir.newRoot;
				// node.L(node's left subtree) grows taller
				if (ir.bGrowTaller == true) {
					// OK, node's left subtree grows taller
					// Let's balance node
					switch (node.bf) {
					case LH:
						AVLNode newRoot = leftBalance(node);
						// After balance, node tree DOES NOT grow taller
						return new InsertResult(newRoot, false);
					case EH:
						node.bf = AVLNode.BalanceFactor.LH;
						return new InsertResult(node, true);
					case RH:
						node.bf = AVLNode.BalanceFactor.EH;
						return new InsertResult(node, false);
					}
				} else {
					return new InsertResult(node, false);
				}
			}
			// greater/equal. new word should be on the right
			else {
				// Whether node.L grows taller?
				InsertResult ir = insert(node.R, newValue);
				// IMPORTANT. After insertion, root node may change
				node.R = ir.newRoot;
				// node.L(node's right subtree) grows taller
				if (ir.bGrowTaller == true) {
					// OK, node's right subtree grows taller
					// Let's balance node
					switch (node.bf) {
					case RH:
						AVLNode newRoot = rightBalance(node);
						// After balance, node tree DOES NOT grow taller
						return new InsertResult(newRoot, false);
					case EH:
						node.bf = AVLNode.BalanceFactor.RH;
						return new InsertResult(node, true);
					case LH:
						node.bf = AVLNode.BalanceFactor.EH;
						return new InsertResult(node, false);
					}
				} else {
					return new InsertResult(node, false);
				}
			}
		}
		// never reach
		return null;
	}
}
