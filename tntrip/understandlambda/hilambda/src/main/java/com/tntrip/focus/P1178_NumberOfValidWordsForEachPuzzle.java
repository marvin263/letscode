package com.tntrip.focus;

import java.util.ArrayList;
import java.util.List;

public class P1178_NumberOfValidWordsForEachPuzzle {
    // a=97, z=122
    public static final boolean[] PUZZLE_CHARS = new boolean[26];
    public static char LEADING_CHAR = 'A';

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
        for (int i = 0; i < PUZZLE_CHARS.length; i++) {
            PUZZLE_CHARS[i] = false;
        }
        LEADING_CHAR = puzzle.charAt(0);
        
        for (int i = 0; i < puzzle.length(); i++) {
            char p = puzzle.charAt(i);
            PUZZLE_CHARS[((int) p) - 97] = true;
        }
    }

    private boolean isWordValid(String word) {
        boolean containLeadingChar = false;
        for (int i = 0; i < word.length(); i++) {
            char w = word.charAt(i);
            if (!containLeadingChar && w == LEADING_CHAR) {
                containLeadingChar = true;
                continue;
            }
            // w comes from puzzle
            if (!PUZZLE_CHARS[((int) w) - 97]) {
                return false;
            }
        }
        return containLeadingChar ? true : false;
    }

    public static void main(String[] args) {
        P1178_NumberOfValidWordsForEachPuzzle p = new P1178_NumberOfValidWordsForEachPuzzle();
        String[] words = new String[]{"aaaa", "asas", "able", "ability", "actt", "actor", "access"};
        String[] puzzles = new String[]{"aboveyz", "abrodyz", "abslute", "absoryz", "actresz", "gaswxyz"};
        System.out.println(p.findNumOfValidWords(words, puzzles));
    }
}
