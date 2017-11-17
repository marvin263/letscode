package com.tntrip.understand.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Created by nuc on 2016/9/10.
 */
public abstract class TraverseByLevel {
    public final static native int w();
    public static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        @Override
        public String toString() {
            return String.valueOf(data);
        }

        public static <N> Node<N> asChild(Node<N> parent, N nowData, boolean asLeftChild) {
            Node<N> cur = new Node<>();
            cur.data = nowData;
            // create root
            if (parent == null) {
                return cur;
            }
            // NOT root
            else {
                if (asLeftChild) {
                    parent.left = cur;
                } else {
                    parent.right = cur;
                }
                return cur;
            }
        }

    }

    public static <T> List<Node<T>> traverseByLevel(Node<T> root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Node<T>> list = new ArrayList<>();

        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> nowNode = queue.poll();
            list.add(nowNode);
            if (nowNode.left != null) {
                queue.add(nowNode.left);
            }
            if (nowNode.right != null) {
                queue.add(nowNode.right);
            }
        }

        return list;
    }

    public static Node<Integer> buildTree() {
        Node<Integer> root = Node.asChild(null, 1, false);
        Node<Integer> node2 = Node.asChild(root, 2, true);
        Node<Integer> node3 = Node.asChild(root, 3, false);

        Node<Integer> node4 = Node.asChild(node2, 4, true);
        Node<Integer> node5 = Node.asChild(node2, 5, false);

        Node<Integer> node6 = Node.asChild(node3, 6, true);
        Node<Integer> node7 = Node.asChild(node3, 7, false);

        Node<Integer> node8 = Node.asChild(node4, 8, true);
        Node<Integer> node9 = Node.asChild(node4, 9, false);

        Node<Integer> node10 = Node.asChild(node5, 10, true);
        Node<Integer> node11 = Node.asChild(node5, 11, false);
        Node<Integer> node12 = Node.asChild(node6, 12, true);
        Node<Integer> node13 = Node.asChild(node6, 13, false);
        Node<Integer> node14 = Node.asChild(node7, 14, true);
        Node<Integer> node15 = Node.asChild(node7, 15, false);
        Node<Integer> node16 = Node.asChild(node8, 16, true);
        return root;
    }

    public static void main(String[] args) {
        Node<Integer> root = buildTree();
        List<Node<Integer>> list = traverseByLevel(root);

        System.out.println(list);


    }

}
