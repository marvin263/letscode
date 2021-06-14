package com.tntrip.focus;

/**
 * @Author libin
 * @Date 2021/6/13
 */
public class P0076_MinimumWindowSubstring {

    public String minWindow(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        int[] expected = new int[26 + 26];
        int[] windows = new int[26 + 26];
        char[] tChars = t.toCharArray();
        for (char c : tChars) {
            int idx = char2Index(c);
            expected[idx] = expected[idx] + 1;
        }

        char[] sChars = s.toCharArray();
        int left = 0;
        int right = 0;
        int slen = s.length();
        // answer[0] - 目前为止，满足要求的最短的子串的长度
        // answer[1], answer[2] - 该子串的起始为止
        int[] answer = new int[]{-1, 0, 0};

        while (right < slen) {
            boolean satisfied = false;
            while (right < slen && !satisfied) {
                expandWindow(sChars[right], windows);
                satisfied = alreadySatisfied(expected, windows);
                if (satisfied) {
                    break;
                }
                right++;
            }
            
            if (!satisfied) {
                break;
            }

            // 肯定满足
            while (alreadySatisfied(expected, windows)) {
                shrinkWindow(sChars[left], windows);
                left++;
            }
            
            int winLen = right - (left - 1) + 1;
            // 最后满足的地方肯定是 left-1
            if (answer[0] == -1 || winLen < answer[0]) {
                answer[0] = winLen;
                answer[1] = left - 1;
                answer[2] = right;
            }
            // 下次开始的地方
            right++;
        }

        return answer[0] == -1 ? "" : s.substring(answer[1], answer[2] + 1);
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
            // 不考察 该字符
            if (expected[i] == 0) {
                continue;
            }
            // 确实考察 该字符，且，当前并不满足
            if (windows[i] < expected[i]) {
                return false;
            }
        }
        // 所有考察的字符都已经满足了要求
        return true;
    }

    private int char2Index(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        if (c >= 'a' && c <= 'z') {
            return (c - 'a') + 26;
        }
        throw new RuntimeException("unexpected char c=" + c);
    }

    public static void main(String[] args) {
        P0076_MinimumWindowSubstring p = new P0076_MinimumWindowSubstring();
        // BANC
        System.out.println(p.minWindow("ADOBECODEBANC", "ABC"));
        // a
        System.out.println(p.minWindow("a", "a"));
        // ""
        System.out.println(p.minWindow("abc", "a"));
        // ""
        System.out.println(p.minWindow("a", "aa"));
    }
}
