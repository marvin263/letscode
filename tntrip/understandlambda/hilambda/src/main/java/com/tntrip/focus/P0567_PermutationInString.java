package com.tntrip.focus;

/**
 * @Author libin
 * @Date 2021/6/13
 */
public class P0567_PermutationInString {
    // s2 中存在一个子串，该子串包含s1中所有字符，且，不包含其他字符
    public boolean checkInclusion(String s1, String s2) {
        if (s2.length() < s1.length()) {
            return false;
        }
        int[] expected = new int[26];
        int[] windows = new int[26];
        char[] s1Chars = s1.toCharArray();
        for (char c : s1Chars) {
            int idx = char2Index(c);
            expected[idx] = expected[idx] + 1;
        }
        int winSize = s1.length();
        char[] s2Chars = s2.toCharArray();
        for (int i = 0; i < winSize; i++) {
            expandWindow(s2Chars[i], windows);
        }

        int left = 0;
        int right = winSize - 1;
        int s2len = s2.length();
        while (right + 1 < s2len
                && !alreadySatisfied(expected, windows)) {
            right++;
            expandWindow(s2Chars[right], windows);
            shrinkWindow(s2Chars[left], windows);
            left++;
        }

        return alreadySatisfied(expected, windows);
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
        P0567_PermutationInString p = new P0567_PermutationInString();
        // true
        System.out.println(p.checkInclusion("ab", "eidbaooo"));

        // false
        System.out.println(p.checkInclusion("ab", "eidboaoo"));

    }
}
