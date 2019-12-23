package com.tntrip;

public class Trie {
    public static class Node {
        public int val = 0;
        public Node[] next = new Node[26];
    }

    private Node root = new Node();

    /**
     * Initialize your data structure here.
     */
    public Trie() {

    }

    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        char[] chars = word.toCharArray();
        Node curNode = root;
        for (char curC : chars) {
            if (curNode.next[curC - 'a'] == null) {
                Node n = new Node();
                curNode.next[curC - 'a'] = n;
                curNode = n;
            } else {
                // drill down
                curNode = curNode.next[curC - 'a'];
            }
        }
        curNode.val++;
    }

    /**
     * Returns if the word is in the trie.
     */
    public boolean search(String word) {
        char[] chars = word.toCharArray();
        Node curNode = root;
        for (char curC : chars) {
            // found
            if (curNode.next[curC - 'a'] != null) {
                curNode = curNode.next[curC - 'a'];
            }
            // NOT found
            else {
                return false;
            }
        }
        return curNode.val > 0;
    }

    /**
     * Returns if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        char[] chars = prefix.toCharArray();
        Node curNode = root;
        for (char curC : chars) {
            // found
            if (curNode.next[curC - 'a'] != null) {
                curNode = curNode.next[curC - 'a'];
            }
            // NOT found
            else {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Trie obj = new Trie();
        obj.insert("apple");
        boolean param_2 = obj.search("apple");
        boolean param_3 = obj.search("app");


        obj.insert("app");
        boolean param_4 = obj.search("apple");
        boolean param_5 = obj.startsWith("app");
        System.out.println(param_2 + ", " + param_3 + ", " + param_4 + ", " + param_5);
    }
}
