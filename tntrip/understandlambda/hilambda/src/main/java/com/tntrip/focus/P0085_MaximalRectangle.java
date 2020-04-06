package com.tntrip.focus;

public class P0085_MaximalRectangle {
    public static int maximalRectangle(char[][] matrix) {
        int verticalLength = matrix.length;
        int horizontalLength = matrix[0].length;

        int[][] horizontal = new int[verticalLength][horizontalLength];
        for (int i = 0; i < verticalLength; i++) {
            horizontal[i][0] = matrix[i][0] == '0' ? 0 : 1;
        }

        int[][] vertical = new int[verticalLength][horizontalLength];
        for (int i = 0; i < horizontalLength; i++) {
            vertical[0][i] = matrix[0][i] == '0' ? 0 : 1;
        }

        int max = 0;
        for (int i = 0; i < matrix.length; i++) {
            char[] cRows = matrix[i];
            for (int j = 0; j < cRows.length; j++) {
                char c = cRows[j];
                // 0
                if (c == '0') {
                    vertical[i][j] = 0;
                    horizontal[i][j] = 0;
                }
                // 1
                else {
                    if (i > 0) {
                        vertical[i][j] = vertical[i - 1][j] + 1;
                    }
                    if (j > 0) {
                        horizontal[i][j] = horizontal[i][j - 1] + 1;
                    }
                }
                max = Math.max(max, (vertical[i][j]) * (horizontal[i][j]));
            }
        }
        return max;
    }

    public static void main(String[] args) {
        char[][] dd = new char[][]{
                {'1', '0', '1', '0', '1'},
                {'0', '1', '1', '1', '1'},
                {'1', '1', '1', '1', '1'},
                {'1', '0', '0', '1', '0'}
        };
        System.out.println(maximalRectangle(dd));
    }

}
