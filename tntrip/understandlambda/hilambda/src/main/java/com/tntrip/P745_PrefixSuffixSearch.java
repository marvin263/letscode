package com.tntrip;

import java.util.HashMap;
import java.util.Map;

public class P745_PrefixSuffixSearch {
    public static class Tire {
        public static class Node {
            // 该word在 words中的索引
            // 如果同样的word在words中出现多次，则为最后出现时的索引
            public int maxIndex = -1;
            public Node[] next = new Node[26];
        }

        private String[] words;
        private Node root = new Node();

        public Tire(String[] words) {
            this.words = words;
        }

        public void insert(String word, int itsIndex) {

        }

        public int maxIndexWithPrefix(String prefix, String suffix) {
            char[] chars = prefix.toCharArray();
            Node curNode = root;
            for (char c : chars) {
                if (curNode.next[c - 'a'] == null) {
                    return -1;
                } else {
                    curNode = curNode.next[c - 'a'];
                }
            }
            int maxIndex = -1;
            if (curNode.maxIndex >= 0) {
                if (suffix.length() == 0 || words[curNode.maxIndex].endsWith(suffix)) {
                    maxIndex = Math.max(curNode.maxIndex, maxIndex);
                }
            } else {

            }
            Node tmp = curNode;
            while (tmp != null) {
                for (Node n : tmp.next) {
                    if (n != null && (suffix.length() == 0 || words[n.maxIndex].endsWith(suffix))) {
                        maxIndex = Math.max(n.maxIndex, maxIndex);
                    }
                }
            }
            

        }


    }

    public P745_PrefixSuffixSearch(String[] words) {

    }

    Map<String, Integer> mapWord2MaxIndex = new HashMap<>();

    public int f(String prefix, String suffix) {

    }


    public void fdfdfd(String prefix, String suffix) {
        if (prefix.length() == 0) {
            mapWord2MaxIndex.getOrDefault(suffix, -1);
        }
        char[] chars = prefix.toCharArray();
        for (char c : chars) {

        }
    }

    public static void main(String[] args) {
        P745_PrefixSuffixSearch p = new P745_PrefixSuffixSearch();
    }
}
