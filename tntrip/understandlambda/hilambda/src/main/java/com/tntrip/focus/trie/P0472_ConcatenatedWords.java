package com.tntrip.focus.trie;

import java.util.ArrayList;
import java.util.List;

public class P0472_ConcatenatedWords {
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        TrieNode root = new TrieNode();
        TrieNode curNode = null;
        TrieNode nextNode = null;
        List<String> rst = new ArrayList<>();

        for (String str : words) {
            curNode = root;
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                nextNode = curNode.setChar(chars[i], i == chars.length - 1);
                curNode = nextNode;
            }
        }

        for (String str : words) {
            curNode = root;
            char[] chars = str.toCharArray();
            int terminatorCount = 0;
            for (int i = 0; i < chars.length; i++) {
                TrieNode n = curNode.getNode(chars[i]);
                if (n.wordTerminator) {
                    terminatorCount++;
                }
                curNode = n;
            }
            if (terminatorCount >= 2) {
                rst.add(str);
            }
        }
        return rst;
    }

    private boolean ccccccc(TrieNode root, char[] chars, int fromIndex, int endIndex, int count) {
        TrieNode curNode = root;
        int terminatorCount = 0;
        for (int i = fromIndex; i < endIndex; i++) {
            TrieNode n = curNode.getNode(chars[i]);
            if (n == null) {
                return false;
            }
            if (n.wordTerminator) {
                return ccccccc(root, chars, i + 1, endIndex, count + 1);
            }
            curNode = n;
        }

        return false;
    }

    public static void main(String[] args) {
        P0472_ConcatenatedWords p = new P0472_ConcatenatedWords();

        // "catsdogcats","dogcatsdog","ratcatdogcat"
        String[] words = new String[]{"cat", "cats", "catsdogcats", "dog", "dogcatsdog", "hippopotamuses", "rat", "ratcatdogcat"};
        System.out.println(p.findAllConcatenatedWordsInADict(words));
    }
}
