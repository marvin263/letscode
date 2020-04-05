package com.tntrip.focus;

public class P1025_DivisorGame {
    public boolean divisorGame(int N) {
        return false;
    }

    public static void main(String[] args) {
        P1025_DivisorGame p = new P1025_DivisorGame();
        int[] numbers = new int[]{1, 2, 3, 4};
        for (int n : numbers) {
            System.out.println(n + ": " + p.divisorGame(n));
        }
    }
}
