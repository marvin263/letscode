package com.tntrip.focus;

import java.util.Stack;

public class P0032_LongestValidParentheses {
    public int longestValidParentheses(String s) {
        int[] dp = new int[s.length()];
        Stack<Integer> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (stack.isEmpty()) {
                if (c == ')') {
                    dp[i] = 1;
                } else {
                    stack.push(i);
                }
            } else {
                if (c == '(') {
                    stack.push(i);
                }
                // ')'
                else {
                    stack.pop();
                }
            }
        }

        while (!stack.isEmpty()) {
            dp[stack.pop()] = 1;
        }


        int maxLength = 0;
        int curLength = 0;
        for (int i = 0; i < dp.length; i++) {
            if (dp[i] == 1) {
                maxLength = Math.max(maxLength, curLength);
                curLength = 0;
            } else {
                curLength++;
            }
        }
        maxLength = Math.max(maxLength, curLength);
        return maxLength;
    }

    public static void main(String[] args) {
        P0032_LongestValidParentheses p = new P0032_LongestValidParentheses();
        System.out.println(p.longestValidParentheses("(()"));//2
        System.out.println(p.longestValidParentheses("()(()"));//2
        System.out.println(p.longestValidParentheses(")()())("));//4

    }

}
