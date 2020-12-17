package com.tntrip.focus.tire;

public class P0720_LongestWordInDictionary {
    public String longestWord(String[] words) {
        return "0";
    }

    public static void main(String[] args) {
        P0720_LongestWordInDictionary p = new P0720_LongestWordInDictionary();
        // world
        String[] words = new String[]{"w", "wo", "wor", "worl", "world"};
        System.out.println(p.longestWord(words));

        // apple
        String[] words2 = new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"};
        System.out.println(p.longestWord(words2));
    }
}
