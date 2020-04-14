package com.tntrip;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

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
        public int col = 0;
        // 0-based
        public int level = 0;
        public final int value;
        public Node left;
        public Node right;

        public static Node create(final int value) {
            Node n = new Node(value);
            return n;
        }

        public Node(final int value) {
            this.value = value;
        }


        public void appendLeft(Node left) {
            this.left = left;
            left.level = this.level + 1;
            left.col = this.col;
        }

        public void appendRight(Node right) {
            this.right = right;
            right.level = this.level + 1;
            right.col = this.col + 1;
        }

        public void incrCol() {
            col++;
        }

        public void incrLevel() {
            col++;
        }
    }

    public static void ddd(int d) {
        int nodeCount = 64;
        int initialValue = nodeCount / 2;
        int step = initialValue / 2;

        List<Integer> initial = Collections.singletonList(initialValue);


        List<String> curLevel = new ArrayList<>();
        for (int i = nodeCount / 2; i >= 1; i++) {
            Node n = Node.create(i + "");
        }
    }

    private Queue<Node> calcNextLevel(Queue<Node> queue) {
        Queue<Node> rstQueue = new ArrayDeque<>();
        int step = queue.peek().value / 2;
        while (!queue.isEmpty()) {
            Node n = queue.remove();

            Node left = Node.create(n.value - step);
            n.appendLeft(left);

            Node right = Node.create(n.value + step);
            n.appendRight(right);
        }
        List<Integer> level = new ArrayList<>(prevLevel.size() * 2);
        for (Integer v : prevLevel) {
            level.add(v - step);
            level.add(v + step);
        }
        return level;
    }

    public static void main(String[] args) {
        BBB b = new BBB();

        System.out.println(b.switchCase('1'));
        System.out.println(b.switchCase('D'));
        System.out.println(b.switchCase('Q'));

        System.out.println(b.letterCasePermutation("a1b"));

        TreeMap<String, String> d1 = new TreeMap<>();
        HashMap<String, String> d2 = new HashMap<>();
        LinkedHashMap<String, String> d3 = new LinkedHashMap<>();
        System.out.println(d2.get(null));
        System.out.println(d3.get(null));
        System.out.println(d1.get(null));
    }
}
