package com.tntrip.focus;

public class P0014_LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        if (strs[0].length() == 0) {
            return "";
        }
        int arrLength = strs.length;

        StringBuilder sb = new StringBuilder("");
        int pos = 0;
        char c = strs[0].charAt(pos);
        while (true) {
            for (int i = 0; i < arrLength; i++) {
                String str = strs[i];
                int length = str.length();
                if (pos >= length || str.charAt(pos) != c) {
                    return sb.toString();
                }
                // the last one
                if (i == strs.length - 1) {
                    sb.append(c);
                    pos++;
                    if (pos >= length) {
                        return sb.toString();
                    } else {
                        c = str.charAt(pos);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        P0014_LongestCommonPrefix p = new P0014_LongestCommonPrefix();
        String[] strs = {"flower", "flow", "1floght"};
        System.out.println(p.longestCommonPrefix(strs));
    }
}
