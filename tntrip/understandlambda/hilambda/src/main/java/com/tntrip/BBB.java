package com.tntrip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BBB {
    public List<String> letterCasePermutation(String S) {
        if (S == null || S.isEmpty()) {
            return Collections.emptyList();
        }
        int length = S.length();
        int curPos = 0;
        List<String> finalRst = new ArrayList<>();
        finalRst.add(S);
        while (curPos <= length - 1) {
            switchEachOne(curPos, finalRst);
            curPos++;
        }
        return finalRst;
    }

    private void switchEachOne(int curPos, List<String> finalRst) {
        char c = finalRst.get(0).charAt(curPos);
        char switchedC = switchCase(c);
        if (c == switchedC) {
            return;
        }
        int length = finalRst.size();
        for (int i = 0; i <= length - 1; i++) {
            String str = finalRst.get(i);
            finalRst.add(str.substring(0, curPos) + switchedC + str.substring(curPos + 1));
        }
    }

    private char switchCase(char c) {
        if (c >= '0' && c <= '9') {
            return c;
        }
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + 32);
        }
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 32);
        }
        return c;
    }


    public static void main(String[] args) {
        BBB b = new BBB();

        System.out.println(b.switchCase('1'));
        System.out.println(b.switchCase('D'));
        System.out.println(b.switchCase('Q'));

        System.out.println(b.letterCasePermutation("a1b"));
    }
}
