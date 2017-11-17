package com.tntrip.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libing2 on 2016/10/11.
 */

public class ClockwiseMatrixAnswer {

    public static void clockwiseTraverseMatrix(List<Integer> listResult,
                                               int[][] twoDimensionArray,
                                               int hStartInclusive, int hEndInclusive,
                                               int vStartInclusive, int vEndInclusive) {
        if (hStartInclusive > hEndInclusive || vStartInclusive > vEndInclusive) {
            return;
        }
        //Top
        for (int i = hStartInclusive; i <= hEndInclusive; i++) {
            listResult.add(twoDimensionArray[vStartInclusive][i]);
        }
        //Right
        for (int i = vStartInclusive + 1; i <= vEndInclusive - 1; i++) {
            listResult.add(twoDimensionArray[i][hEndInclusive]);
        }

        //Bottom
        if (vEndInclusive > vStartInclusive) {
            for (int i = hEndInclusive; i >= hStartInclusive; i--) {
                listResult.add(twoDimensionArray[vEndInclusive][i]);
            }
        }
        //Left
        if (hEndInclusive > hStartInclusive) {
            for (int i = vEndInclusive - 1; i >= vStartInclusive + 1; i--) {
                listResult.add(twoDimensionArray[i][hStartInclusive]);
            }
        }
        clockwiseTraverseMatrix(listResult,
                twoDimensionArray,
                ++hStartInclusive, --hEndInclusive,
                ++vStartInclusive, --vEndInclusive);
    }


    private static void displayTwoDimension(int[][] twoDimensionArray) {
        for (int[] aTwoDimensionArray : twoDimensionArray) {
            System.out.println(toString(aTwoDimensionArray));
        }
    }

    private static String toString(int[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(leftPad(a[i] + "", ' ', 2));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    private static String leftPad(String orgn, char padChar, int totalLength) {
        if (orgn.length() >= totalLength) {
            return orgn;
        }
        StringBuilder sb = new StringBuilder(totalLength);
        sb.setLength(totalLength);
        int offset = totalLength - orgn.length();
        for (int i = 0; i < orgn.length(); i++) {
            sb.setCharAt(offset + i, orgn.charAt(i));
        }
        for (int i = offset - 1; i >= 0; i--) {
            sb.setCharAt(i, padChar);
        }
        return sb.toString();
    }

    private static void print(int[][] array) {

        displayTwoDimension(array);

        List<Integer> listResult = new ArrayList<>();
        clockwiseTraverseMatrix(listResult, array, 0, array[0].length - 1, 0, array.length - 1);
        System.out.println("Result is:\n" + listResult);
        System.out.println();
    }

    public static void main(String[] args) {
        int[][] array0 = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
        };
        int[][] array1 = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 15552},
        };

        int[][] array2 = new int[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
        };

        int[][] array3 = new int[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16},
        };
        int[][][] input = {array0, array1, array2, array3};
        for (int[][] array : input) {
            print(array);
        }
    }
}
