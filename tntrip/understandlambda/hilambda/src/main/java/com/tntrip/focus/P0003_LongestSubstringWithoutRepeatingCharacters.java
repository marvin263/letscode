package com.tntrip.focus;

import java.util.HashSet;
import java.util.Set;

public class P0003_LongestSubstringWithoutRepeatingCharacters {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] chars = s.toCharArray();

        Set<Character> set = new HashSet<>();
        int curLength = 0;
        int maxLength = -1;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            curLength = 1;
            set.clear();
            set.add(c);
            for (int j = i + 1; j < chars.length; j++) {
                if (!set.contains(chars[j])) {
                    curLength++;
                    set.add(chars[j]);
                } else {
                    break;
                }
            }
            maxLength = Math.max(curLength, maxLength);
        }
        return maxLength;
    }


    public int lengthOfLongestSubstring2(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] chars = s.toCharArray();
        int[][] dp = new int[chars.length][chars.length];

        for (int i = 0; i < chars.length; i++) {
            char ci = chars[i];
            dp[i][i] = 1;
            for (int j = i + 1; j < chars.length; j++) {
                char cj = chars[j];
                if (ci == cj) {
                    dp[i][j] = dp[i][j - 1];
                } else {
                    boolean duplicated = false;
                    for (int k = i; k <= j - 1; k++) {
                        if (chars[k] == cj) {
                            duplicated = true;
                            break;
                        }
                    }
                    dp[i][j] = duplicated ? dp[i][j - 1] : dp[i][j - 1] + 1;
                }
            }
        }
        return dp[0][chars.length - 1];
    }

    public static void main(String[] args) {
        P0003_LongestSubstringWithoutRepeatingCharacters p = new P0003_LongestSubstringWithoutRepeatingCharacters();
        System.out.println(p.lengthOfLongestSubstring2("pwwkew"));//3
        System.out.println(p.lengthOfLongestSubstring2("abcabcbb"));//3
        System.out.println(p.lengthOfLongestSubstring2("bbbbb"));//1
    }

}
