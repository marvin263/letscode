package com.tntrip;

import java.util.ArrayList;
import java.util.List;

public class StreamChecker {
    public static class Trie {
        public static class Node {
            // 该word在 words中的索引
            // 如果同样的word在words中出现多次，则为最后出现时的索引
            public int wordIndex = -1;
            public Node[] next = new Node[26];
        }

        private String[] words;
        private Node root = new Node();

        public Trie(String[] words) {
            this.words = words;
        }

        public void insert(int wordIndex, boolean reverse) {
            String word = words[wordIndex];
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
            curNode.wordIndex = wordIndex;
        }

        public Node findNode(List<Character> list) {
            Node curNode = root;
            for (int i = list.size() - 1; i >= 0; i--) {
                char c = list.get(i);
                if (curNode.next[c - 'a'] != null) {
                    curNode = curNode.next[c - 'a'];
                    if (curNode.wordIndex >= 0) {
                        return curNode;
                    }
                } else {
                    return null;
                }
            }
            return null;
        }
    }

    private Trie trie;
    private List<Character> list = new ArrayList<>();

    public StreamChecker(String[] words) {
        trie = new Trie(words);
        for (int i = 0; i < words.length; i++) {
            trie.insert(i, true);
        }
    }

    public boolean query(char letter) {
        list.add(letter);
        Trie.Node node = trie.findNode(list);
        return node != null;
    }

    public static void main(String[] args) {

        StreamChecker p = new StreamChecker(new String[]{"cd", "f", "kl"}); // init the dictionary.
        System.out.println(p.query('a'));          // return false
        System.out.println(p.query('b'));          // return false
        System.out.println(p.query('c'));          // return false
        System.out.println(p.query('d'));          // return true, because 'cd' is in the wordlist
        System.out.println(p.query('e'));          // return false
        System.out.println(p.query('f'));          // return true, because 'f' is in the wordlist
        System.out.println(p.query('g'));          // return false
        System.out.println(p.query('h'));          // return false
        System.out.println(p.query('i'));          // return false
        System.out.println(p.query('j'));          // return false
        System.out.println(p.query('k'));          // return false
        System.out.println(p.query('l'));          // return true, because 'kl' is in the wordlist

    }
}
