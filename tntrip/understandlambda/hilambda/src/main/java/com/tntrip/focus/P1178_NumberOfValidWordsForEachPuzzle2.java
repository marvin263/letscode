package com.tntrip.focus;

import java.util.ArrayList;
import java.util.List;

public class P1178_NumberOfValidWordsForEachPuzzle2 {
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

    private static int LEADING_CHAR_INDEX = 0;
    private static int PUZZLE_INT = 0;

    public List<Integer> findNumOfValidWords(String[] words, String[] puzzles) {
        List<Integer> answers = new ArrayList<>(puzzles.length);
        for (String puzzle : puzzles) {
            reset(puzzle);
            int count = 0;
            for (String word : words) {
                if (isWordValid(word)) {
                    count++;
                }
            }
            answers.add(count);
        }
        return answers;
    }

    private void reset(String puzzle) {
        PUZZLE_INT = 0;
        LEADING_CHAR_INDEX = ((int) puzzle.charAt(0)) - 97;
        for (int i = 0; i < puzzle.length(); i++) {
            char p = puzzle.charAt(i);
            // 包含该字母，则将其置为1
            PUZZLE_INT = PUZZLE_INT | MASK[((int) p) - 97];
        }
    }

    private boolean isWordValid(String word) {
        boolean containLeadingChar = false;
        for (int i = 0; i < word.length(); i++) {
            char w = word.charAt(i);
            if (!containLeadingChar && LEADING_CHAR_INDEX == (((int) w) - 97)) {
                containLeadingChar = true;
                continue;
            }
            // NOT contains w
            if ((PUZZLE_INT & (MASK[((int) w) - 97])) == 0) {
                return false;
            }
        }
        return containLeadingChar ? true : false;
    }

    public static void main(String[] args) {
        P1178_NumberOfValidWordsForEachPuzzle2 p = new P1178_NumberOfValidWordsForEachPuzzle2();
        String[] words = new String[]{"aaaa", "asas", "able", "ability", "actt", "actor", "access"};
        String[] puzzles = new String[]{"aboveyz", "abrodyz", "abslute", "absoryz", "actresz", "gaswxyz"};
        System.out.println(p.findNumOfValidWords(words, puzzles));
        System.out.println(Integer.toBinaryString(5));
    }
}
