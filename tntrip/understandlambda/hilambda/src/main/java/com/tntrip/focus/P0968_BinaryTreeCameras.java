package com.tntrip.focus;

public class P0968_BinaryTreeCameras {
    public int minCameraCover(TreeNode root) {
        return -1;
    }

    private TreeNode toTree(Integer[] intValues) {
        if (intValues.length == 0) {
            return null;
        }
        if (intValues[0] == null) {
            throw new RuntimeException("Root node value should not be null");
        }
        int last = intValues.length - 1;
        int mid = intValues.length / 2;
        TreeNode[] nodes = new TreeNode[mid + 1];
        nodes[0] = new TreeNode(intValues[0]);
        for (int i = 0; i <= mid; i++) {
            int left = 2 * i + 1;
            if (left <= last && intValues[left] != null) {
                nodes[i].left = new TreeNode(intValues[left]);
            }
            int right = 2 * i + 2;
            if (right <= last && intValues[right] != null) {
                nodes[i].right = new TreeNode(intValues[right]);
            }

            if (left <= mid) {
                nodes[left] = nodes[i].left;
            }
            if (right <= mid) {
                nodes[right] = nodes[i].right;
            }
        }
        return nodes[0];
    }
    // 广度优先构建
    private TreeNode bfs(Integer[] intValues) {
        if (intValues.length == 0) {
            return null;
        }
        if (intValues[0] == null) {
            throw new RuntimeException("Root node value should not be null");
        }
        TreeNode root = new TreeNode(intValues[0]);
        //TreeNode[] nodes = new TreeNode[mid + 1];
        //nodes[0] = new TreeNode(intValues[0]);
        return null;
    }

    public static void main(String[] args) {
        P0968_BinaryTreeCameras p = new P0968_BinaryTreeCameras();
        //Integer[] intValues1 = new Integer[]{0, 0, null, 0, 0};
        Integer[] intValues2 = new Integer[]{0, 0, null, 0, null, 0, null, null, 0};
        //TreeNode treeNode1 = p.toTree(intValues1);
        TreeNode treeNode2 = p.toTree(intValues2);
        //System.out.println(treeNode1.toString() + treeNode2.toString());
    }

}
