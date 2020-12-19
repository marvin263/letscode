package com.tntrip.focus.trie;

/**
 * 211. Design Add and Search Words Data Structure
 * <p>
 * https://leetcode-cn.com/problems/design-add-and-search-words-data-structure/
 */
public class P0211_DesignAddSearchWordsDataStructure {
    public static class TrieNode {
        private boolean wordTerminator;
        public TrieNode[] children;

        public TrieNode setChar(char c, boolean wordTerminator) {
            children = children == null ? new TrieNode[26] : children;
            int idx = toIdx(c);
            children[idx] = children[idx] == null ? new TrieNode() : children[idx];
            if (wordTerminator) {
                children[idx].wordTerminator = true;
            }
            return children[idx];
        }

        public boolean containsChar(char c) {
            return children != null && children[toIdx(c)] != null;
        }

        public boolean charIsWordTerminator(char c) {
            return containsChar(c) && children[toIdx(c)].wordTerminator;
        }

        public boolean existWordTerminator() {
            if (children == null) {
                return false;
            }
            for (TrieNode n : children) {
                if (n != null && n.wordTerminator) {
                    return true;
                }
            }
            return false;
        }
    }

    public static int toIdx(char c) {
        return (int) c - (int) 'a';
    }

    public static char toChar(int idx) {
        return (char) ((int) 'a' + idx);
    }

    private final TrieNode root;

    /**
     * Initialize your data structure here.
     */
    public P0211_DesignAddSearchWordsDataStructure() {
        root = new TrieNode();
    }


    /**
     * Adds a word into the data structure.
     */
    public void addWord(String word) {
        TrieNode cur = root;
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            cur = cur.setChar(chars[i], i == chars.length - 1);
        }
    }

    /**
     * Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter.
     */
    public boolean search(String word) {
        char[] chars = word.toCharArray();
        return match(chars, 0, chars.length - 1, root);
    }

    public boolean match(char[] chars, int fromIndex, int endIndex, TrieNode root) {
        TrieNode cur = root;
        for (int i = fromIndex; i <= endIndex; i++) {
            char c = chars[i];
            if (c != '.') {
                // c是最后一个字符，该字符必须是结束符
                if (i == chars.length - 1) {
                    return cur.charIsWordTerminator(c);
                }
                // c不是最后一个字符，c字符必须存在
                else if (cur.containsChar(c)) {
                    cur = cur.children[toIdx(c)];
                }
                // c不是最后一个字符，但，c字符又不存
                else {
                    return false;
                }
            } else {
                // .是最后一个字符，该字符必须是结束符
                if (i == chars.length - 1) {
                    return cur.existWordTerminator();
                }
                // .不是最后一个字符，则，.后面的字符必须存在
                else if (cur.children != null) {
                    // .后面的字符必须匹配后面的
                    for (TrieNode child : cur.children) {
                        if (child != null && match(chars, i + 1, endIndex, child)) {
                            return true;
                        }
                    }
                    return false;
                }
                // .不是最后一个字符，则，.后面的字符还不存在
                else {
                    return false;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        P0211_DesignAddSearchWordsDataStructure obj = new P0211_DesignAddSearchWordsDataStructure();
        String[] inputs = new String[]{"bad", "dad", "mad",};
        for (String input : inputs) {
            obj.addWord(input);
        }

        String[] sssss = new String[]{".ad"};
        for (String input : sssss) {
            System.out.println(obj.search(input));
        }
    }
}
