package com.tntrip;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WordFilter {
    public static class Tire {
        public static class Node {
            // 该word在 words中的索引
            // 如果同样的word在words中出现多次，则为最后出现时的索引
            public int lastWordIndex = -1;
            public Node[] next = new Node[26];
        }

        private String[] words;
        private Node root = new Node();

        public Tire(String[] words) {
            this.words = words;
        }

        public void insert(String word, int wordIndex, boolean reverse) {
            char[] chars = word.toCharArray();
            Node curNode = root;
            if (!reverse) {
                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i];
                    if (curNode.next[c - 'a'] != null) {
                        curNode = curNode.next[c - 'a'];
                    } else {
                        Node n = new Node();
                        curNode.next[c - 'a'] = n;
                        curNode = n;
                    }
                }
            } else {
                for (int i = chars.length - 1; i >= 0; i--) {
                    char c = chars[i];
                    if (curNode.next[c - 'a'] != null) {
                        curNode = curNode.next[c - 'a'];
                    } else {
                        Node n = new Node();
                        curNode.next[c - 'a'] = n;
                        curNode = n;
                    }
                }
            }
            curNode.lastWordIndex = Math.max(curNode.lastWordIndex, wordIndex);
        }

        /**
         * "abc"时，则找到 指向c的节点（c.next中某个idx是否null尚不知道）
         * 并不在意 返回的node是否是word
         *
         * @param word
         * @return
         */
        public Node findNode(String word) {
            if (word.length() == 0) {
                return null;
            }
            char[] chars = word.toCharArray();
            Node curNode = root;
            for (char c : chars) {
                if (curNode.next[c - 'a'] != null) {
                    curNode = curNode.next[c - 'a'];
                } else {
                    return null;
                }
            }
            return curNode;
        }

        public int endsWithSuffixMaxIndex(Node node, String suffix) {
            if (node == null) {
                return -1;
            }
            int finalMaxIndex = -1;
            Queue<Node> queue = new LinkedList<>();
            queue.offer(node);
            while (!queue.isEmpty()) {
                Node n = queue.poll();
                // 该节点 存在 对应的word
                if (n.lastWordIndex >= 0
                        && (suffix.length() == 0 || words[n.lastWordIndex].endsWith(suffix))) {
                    finalMaxIndex = Math.max(finalMaxIndex, n.lastWordIndex);
                }
                // 该节点 不存在 对应的word
                else {

                }
                // 该node所有的子节点
                for (Node subNode : n.next) {
                    if (subNode != null) {
                        queue.offer(subNode);
                    }
                }
            }
            return finalMaxIndex;
        }
        
        public int maxIndex(Node node) {
            if (node == null) {
                return -1;
            }
            int finalMaxIndex = -1;
            Queue<Node> queue = new LinkedList<>();
            queue.offer(node);
            while (!queue.isEmpty()) {
                Node n = queue.poll();
                // 该节点 存在 对应的word
                if (n.lastWordIndex >= 0) {
                    finalMaxIndex = Math.max(finalMaxIndex, n.lastWordIndex);
                }
                // 该节点 不存在 对应的word
                else {

                }
                // 该node所有的子节点
                for (Node subNode : n.next) {
                    if (subNode != null) {
                        queue.offer(subNode);
                    }
                }
            }
            return finalMaxIndex;
        }
    }

    private int length;
    private Tire forwardTrie;
    private Tire reverseTrie;
    private Map<String, Integer> existed = new HashMap<>();

    public WordFilter(String[] words) {
        this.length = words.length;

        forwardTrie = new Tire(words);
        reverseTrie = new Tire(words);
        for (int i = words.length - 1; i >= 0; i--) {
            forwardTrie.insert(words[i], i, false);
            reverseTrie.insert(words[i], i, true);
        }
    }

    public int f(String prefix, String suffix) {
        String key = prefix + "A" + suffix;
        if (existed.containsKey(key)) {
            return existed.get(key);
        }

        int rst = -1;
        try {
            if (prefix.length() == 0 && suffix.length() == 0) {
                rst = length - 1;
                return rst;
            }
            if (prefix.length() == 0) {
                StringBuilder reversedSuffix = new StringBuilder(suffix.length());
                for (int i = suffix.length() - 1; i >= 0; i--) {
                    reversedSuffix.append(suffix.charAt(i));
                }
                Tire.Node node = reverseTrie.findNode(reversedSuffix.toString());
                rst = reverseTrie.maxIndex(node);
                return rst;
            } else {
                Tire.Node node = forwardTrie.findNode(prefix);
                rst = forwardTrie.endsWithSuffixMaxIndex(node, suffix);
                return rst;
            }
        } finally {
            existed.put(key, rst);
        }
    }


    public static void main(String[] args) {
        String[] words = {"pop"};
        WordFilter p = new WordFilter(words);

        System.out.println(p.f("", "")); // 返回 0
        System.out.println(p.f("", "p")); // 返回 0
        System.out.println(p.f("", "op")); // 返回 0 
    }
}
