package com.tntrip;

import com.tntrip.focus.P0887_SuperEggDrop;

import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class BBB {
    public List<String> letterCasePermutation(String S) {
        if (S == null || S.isEmpty()) {
            return Collections.emptyList();
        }
        int length = S.length();
        int curPos = 0;
        List<String> finalRst = new ArrayList<>();
        finalRst.add(S);
        while (curPos <= length - 1) {
            switchEachOne(curPos, finalRst);
            curPos++;
        }
        return finalRst;
    }

    private void switchEachOne(int curPos, List<String> finalRst) {
        char c = finalRst.get(0).charAt(curPos);
        char switchedC = switchCase(c);
        if (c == switchedC) {
            return;
        }
        int length = finalRst.size();
        for (int i = 0; i <= length - 1; i++) {
            String str = finalRst.get(i);
            finalRst.add(str.substring(0, curPos) + switchedC + str.substring(curPos + 1));
        }
    }

    private char switchCase(char c) {
        if (c >= '0' && c <= '9') {
            return c;
        }
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + 32);
        }
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 32);
        }
        return c;
    }

    public static class Node {
        // 0-based
        public int level = 0;
        // 0-based
        public int col = 0;
        public final int value;
        public Node left;
        public Node right;

        public static Node create(final int value) {
            Node n = new Node(value);
            return n;
        }

        private Node(final int value) {
            this.value = value;
        }

        public void appendLeft(Node left) {
            this.left = left;
            left.level = this.level + 1;
            left.col = this.col - 1;
        }

        public void appendRight(Node right) {
            this.right = right;
            right.level = this.level + 1;
            right.col = this.col;
        }

        public void offsetNodeColIndex(int h) {
            this.col = h - 1 + this.col;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "col=" + col +
                    ", level=" + level +
                    ", value=" + value +
                    '}';
        }
    }

    private int treeHeight(Node n) {
        return n == null ? 0 : (1 + Math.max(treeHeight(n.left), treeHeight(n.right)));
    }


    private Queue<Node> addNodes(Queue<Node> queue) {
        Queue<Node> rstQueue = new ArrayDeque<>();
        if (queue.isEmpty()) {
            return rstQueue;
        }

        int step = queue.peek().value / 2;
        while (!queue.isEmpty()) {
            Node parent = queue.remove();

            Node left = Node.create(parent.value - step);
            parent.appendLeft(left);
            rstQueue.offer(left);

            Node right = Node.create(parent.value + step);
            parent.appendRight(right);
            rstQueue.offer(right);
        }
        System.out.println("Current level node count: " + queue.size() + ", Next level node count: " + rstQueue.size());
        return rstQueue;
    }

    private Node createTree() {
        int nodeCount = 8195;
        int rootValue = (nodeCount+1) / 2;
        int height = (int) Math.ceil(P0887_SuperEggDrop.log(nodeCount + 1, 2));
        System.out.println(height);
        
        Node root = Node.create(rootValue);
        
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(root);

        Queue<Node> nextQueue = queue;
        for (int i = 1; i < height; i++) {
            nextQueue = addNodes(nextQueue);
        }
        resetTreeColIndex(treeHeight(root), root);
        return root;
    }

    private void resetTreeColIndex(int h, Node n) {
        if (n == null) {
            return;
        }

        n.offsetNodeColIndex(h);
        resetTreeColIndex(h, n.left);
        resetTreeColIndex(h, n.right);

    }

    private List<Node> bfs(Node root) {
        List<Node> rst = new ArrayList<>();
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node n = queue.remove();
            rst.add(n);
            if (n.left != null) {
                queue.offer(n.left);
            }
            if (n.right != null) {
                queue.offer(n.right);
            }
        }
        return rst;
    }


    public static void main(String[] args) {
        System.out.println(String.format("SelectionKey.OP_READ == 1 << 0 == %d", SelectionKey.OP_READ));
        System.out.println(String.format("SelectionKey.OP_WRITE = 1 << 2 == %d", SelectionKey.OP_WRITE));
        System.out.println(String.format("SelectionKey.OP_CONNECT = 1 << 3 == %d", SelectionKey.OP_CONNECT));
        System.out.println(String.format("SelectionKey.OP_ACCEPT = 1 << 4 == %d", SelectionKey.OP_ACCEPT));
        
        BBB b = new BBB();
        Node root = b.createTree();
        List<Node> list = b.bfs(root);

        list.sort(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n1.col != n2.col ?
                        n1.col - n2.col
                        : ((n1.level != n2.level) ? n1.level - n2.level : n1.value - n2.value);
            }
        });

        for (Node n : list) {
            System.out.println(n);
        }
    }

}
