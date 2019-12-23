package com.tntrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class P1178_NumberOfValidWordsForEachPuzzle4 {
    // a=97, z=122
    public static final int[] MASK = {
            1 << 0,//a
            1 << 1,
            1 << 2,
            1 << 3,
            1 << 4,
            1 << 5,
            1 << 6,
            1 << 7,
            1 << 8,
            1 << 9,
            1 << 10,
            1 << 11,
            1 << 12,
            1 << 13,
            1 << 14,
            1 << 15,
            1 << 16,
            1 << 17,
            1 << 18,
            1 << 19,
            1 << 20,
            1 << 21,
            1 << 22,
            1 << 23,
            1 << 24,
            1 << 25,//z
    };

    public List<Integer> findNumOfValidWords(String[] words, String[] puzzles) {
        Map<Integer, Integer> mapWordNum2Count = new HashMap<>();
        for (String word : words) {
            int wordNum = word2num(word);
            if (wordNum == 0) {
                continue;
            }
            Integer count = mapWordNum2Count.get(wordNum);
            mapWordNum2Count.put(wordNum, count == null ? 1 : count + 1);
        }

        List<Integer> answers = new ArrayList<>(puzzles.length);
        for (int i = 0; i < puzzles.length; i++) {
            Set<Integer> puzzleNums = puzzleNums(puzzles[i]);
            int count = 0;
            for (Integer puzzleNum : puzzleNums) {
                Integer curCount = mapWordNum2Count.get(puzzleNum);
                count += (curCount == null ? 0 : curCount);
            }
            answers.add(count);
        }
        return answers;
    }

    private int word2num(String word) {
        Set<Character> encountered = new HashSet<>(8);
        int wordNum = 0;
        for (int i = 0; i < word.length(); i++) {
            char w = word.charAt(i);
            encountered.add(w);
            if (encountered.size() >= 8) {
                return 0;
            }
            // 包含该字母，则将其置为1
            wordNum = wordNum | MASK[((int) w) - 97];
        }
        return wordNum;
    }

    private Set<Integer> puzzleNums(String puzzle) {
        int leadingBit = MASK[((int) puzzle.charAt(0)) - 97];
        int pNum = 0;
        for (int i = 0; i < puzzle.length(); i++) {
            pNum = pNum | MASK[((int) puzzle.charAt(i)) - 97];
        }

        Set<Integer> set = new HashSet<>(64);
        for (int i = pNum; i > 0; i = (i - 1) & pNum) {
            if ((i & leadingBit) != 0) {
                set.add(i);
            }
        }
        return set;
    }
    
    public static void main(String[] args) {
        P1178_NumberOfValidWordsForEachPuzzle4 p = new P1178_NumberOfValidWordsForEachPuzzle4();
        String[] words = new String[]{"aaaa", "asas", "able", "ability", "actt", "actor", "access"};
        String[] puzzles = new String[]{"aboveyz", "abrodyz", "abslute", "absoryz", "actresz", "gaswxyz"};
        System.out.println(p.findNumOfValidWords(words, puzzles));

        Set<Integer> abc = p.puzzleNums("abcdefg");
        System.out.println(abc.size());
        for (Integer integer : abc) {
            System.out.println(Integer.toBinaryString(integer));
        }
    }
}
