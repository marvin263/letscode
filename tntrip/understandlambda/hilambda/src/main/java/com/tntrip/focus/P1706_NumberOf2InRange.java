package com.tntrip.focus;

public class P1706_NumberOf2InRange {
    public int numberOf2sInRange(int n) {
        String str = n + "";
        return 1;
    }

    public static void main(String[] args) {
        P1706_NumberOf2InRange p = new P1706_NumberOf2InRange();
        int[] numbers = new int[]{1, 2, 3, 4, 1000};
        for (int n : numbers) {
            System.out.println(n + ": " + p.numberOf2sInRange(n));
        }
    }
}
