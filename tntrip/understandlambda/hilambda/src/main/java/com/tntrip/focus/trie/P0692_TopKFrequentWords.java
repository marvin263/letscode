package com.tntrip.focus.trie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P0692_TopKFrequentWords {
    public List<String> topKFrequent1(String[] words, int k) {
        Map<String, Integer> map = new HashMap<>();
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }

        Comparator<Map.Entry<String, Integer>> s = (m1, m2) -> {
            if (m1.getValue().equals(m2.getValue())) {
                return m1.getKey().compareTo(m2.getKey());
            }
            return m2.getValue() - m1.getValue();
        };

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(s);

        List<String> ddd = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            ddd.add(list.get(i).getKey());
        }
        return ddd;
    }

    public static void main(String[] args) {
        P0692_TopKFrequentWords p = new P0692_TopKFrequentWords();
        // ["i", "love"]
        String[] words = new String[]{"i", "love", "leetcode", "i", "love", "coding"};
        System.out.println(p.topKFrequent1(words, 2));


        // ["the", "is", "sunny", "day"]
        String[] words2 = new String[]{"the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"};
        System.out.println(p.topKFrequent1(words2, 4));

    }
}
