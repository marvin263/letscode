package com.tree.trietree;

public class TrieBranch implements TrieNode {
    public static final int DOLLAR_END_FLAG_INDEX = 0;
    public TrieNode[] children = new TrieNode[1 + 10 + 1 + 26];

    private TrieBranch() {
    }

    public static TrieBranch createBranchNode() {
        return new TrieBranch();
    }

    @Override
    public boolean bLeaf() {
        return false;
    }

}
