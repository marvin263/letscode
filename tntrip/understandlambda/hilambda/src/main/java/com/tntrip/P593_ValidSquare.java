package com.tntrip;

import java.util.Arrays;

public class P593_ValidSquare {
    public boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        int[][] array = new int[4][2];
        array[0] = p1;
        array[1] = p2;
        array[2] = p3;
        array[3] = p4;
        int[] distances = new int[6];
        int idx = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                distances[idx] = distance(array[i], array[j]);
                idx++;
            }
        }
        Arrays.sort(distances);
        return distances[0] != 0 && distances[0] == distances[1] &&
                distances[0] == distances[2] &&
                distances[0] == distances[3] &&
                distances[0] * 2 == distances[5] &&
                distances[4] == distances[5];
    }

    private int distance(int[] p1, int[] p2) {
        return ((p1[0] - p2[0]) * (p1[0] - p2[0])) + ((p1[1] - p2[1]) * (p1[1] - p2[1]));
    }

    public static void main(String[] args) {
        P593_ValidSquare p = new P593_ValidSquare();
        int[] p1 = {0, 0}, p2 = {1, 1}, p3 = {1, 0}, p4 = {0, 1};
        System.out.println(p.validSquare(p1, p2, p3, p4));
    }
}
