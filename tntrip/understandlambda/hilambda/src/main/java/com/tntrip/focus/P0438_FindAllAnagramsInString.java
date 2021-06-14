package com.tntrip.focus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author libin
 * @Date 2021/6/14
 */
public class P0438_FindAllAnagramsInString {
    public List<Integer> findAnagrams(String s, String p) {
        if (s.length() < p.length()) {
            return Collections.emptyList();
        }

        int[] expected = new int[26];
        int[] windows = new int[26];
        char[] pChars = p.toCharArray();
        for (char c : pChars) {
            int idx = char2Index(c);
            expected[idx] = expected[idx] + 1;
        }
        int winSize = p.length();
        char[] sChars = s.toCharArray();
        for (int i = 0; i < winSize; i++) {
            expandWindow(sChars[i], windows);
        }

        List<Integer> list = new ArrayList<>();
        if (alreadySatisfied(expected, windows)) {
            list.add(0);
        }

        int left = 0;
        int right = winSize - 1;
        int slen = s.length();
        while (right + 1 < slen) {
            right++;
            expandWindow(sChars[right], windows);
            shrinkWindow(sChars[left], windows);
            left++;
            if (alreadySatisfied(expected, windows)) {
                list.add(left);
            }
        }
        return list;
    }

    private void expandWindow(char c, int[] windows) {
        int idx = char2Index(c);
        windows[idx] = windows[idx] + 1;
    }

    private void shrinkWindow(char c, int[] windows) {
        int idx = char2Index(c);
        windows[idx] = windows[idx] - 1;
    }

    private boolean alreadySatisfied(int[] expected, int[] windows) {
        for (int i = 0; i < expected.length; i++) {
            if (windows[i] != expected[i]) {
                return false;
            }
        }
        // 所有考察的字符都已经满足了要求
        return true;
    }

    private int char2Index(char c) {
        if (c >= 'a' && c <= 'z') {
            return (c - 'a');
        }
        throw new RuntimeException("unexpected char c=" + c);
    }

    public static void main(String[] args) {
        P0438_FindAllAnagramsInString p = new P0438_FindAllAnagramsInString();
        // 0, 6
        System.out.println(p.findAnagrams("cbaebabacd", "abc"));
        // 0, 1, 2
        System.out.println(p.findAnagrams("abab", "ab"));
    }
}
