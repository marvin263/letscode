package com.tntrip.focus.trie;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class P0720_LongestWordInDictionary {
    public String longestWord(String[] words) {
        TrieNode root = new TrieNode();
        TrieNode curNode = null;
        TrieNode nextNode = null;
        for (String str : words) {
            curNode = root;
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                nextNode = curNode.setChar(chars[i], i == chars.length - 1);
                curNode = nextNode;
            }
        }

        String strLongest = "";
        for (String str : words) {
            char[] chars = str.toCharArray();
            curNode = root;
            boolean legal = true;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                TrieNode n = curNode.getNode(c);
                curNode = n;
                if (!n.wordTerminator) {
                    legal = false;
                    break;
                }
            }

            if (legal) {
                if (str.length() > strLongest.length()) {
                    strLongest = str;
                } else if (str.length() == strLongest.length()) {
                    if (str.compareTo(strLongest) < 0) {
                        strLongest = str;
                    }
                }
            }
        }
        return strLongest;
    }

    public String longestWord1(String[] words) {
        Comparator<String> s = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.length() == s2.length()) {
                    return s2.compareTo(s1);
                }
                return s1.length() - s2.length();
            }
        };

        Set<String> set = Arrays.stream(words).collect(Collectors.toSet());

        Arrays.sort(words, s);

        for (int i = words.length - 1; i >= 0; i--) {
            String word = words[i];

            int idx = -1;
            for (idx = word.length() - 1; idx >= 1; idx--) {
                if (set.contains(word.substring(0, idx))) {

                } else {
                    break;
                }
            }

            if (idx == 0) {
                return word;
            }
        }

        return "";
    }


    public static void main(String[] args) {
        P0720_LongestWordInDictionary p = new P0720_LongestWordInDictionary();
        // "world"
        String[] words = new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"};
        System.out.println(p.longestWord(words));

        // "yyd"
        String[] words1 = new String[]{"n", "j", "sl", "yyd", "slo", "jk", "jkdt", "y", "yy"};
        System.out.println(p.longestWord(words1));

        // apple
        //String[] words2 = new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"};
        //System.out.println(p.longestWord(words2));
    }
}
