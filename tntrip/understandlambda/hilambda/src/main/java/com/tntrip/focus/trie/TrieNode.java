package com.tntrip.focus.trie;

/**
 * 211. Design Add and Search Words Data Structure
 * <p>
 * https://leetcode-cn.com/problems/design-add-and-search-words-data-structure/
 */
public class TrieNode {
    public boolean wordTerminator;
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

    public TrieNode getNode(char c) {
        return children == null ? null : children[toIdx(c)];
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

    public static int toIdx(char c) {
        return (int) c - (int) 'a';
    }

    public static char toChar(int idx) {
        return (char) ((int) 'a' + idx);
    }

}
