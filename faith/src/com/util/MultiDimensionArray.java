package com.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiDimensionArray {

    public static void twoDim() {
        int[][] x = new int[2][];
        x[0] = new int[] { 1 };
        x[1] = new int[] { 100, 200, 300 };
        System.out.println(Arrays.deepToString(x));// [[1], [100,200,300]]
        System.out.println(x.length);// 2
        System.out.println(Arrays.toString(x[0]));// [1]
    }

    /**
     * <pre>
     *         r&c    r0    r1    r2    r3    r4    r5
     *         c0    0    0    1    1    1    1
     *         c1    1    0    1    0    0    0
     *         c2    1    0    0    0    1    1
     *         c3    1    1    1    0    1    1
     * </pre>
     * 
     * First row: row header
     * 
     * First column: column header
     * 
     * @param fullPath
     * @return
     */
    public static String[][] construct_2D_Array(String fullPath) {
        List<List<String>> listAllRow = buildList(fullPath);
        // row 0 is always header
        if (listAllRow.size() <= 1) {
            return new String[0][0];
        }
        String[][] matrix = new String[listAllRow.size() - 1][];
        // row 0 is always header
        for (int row = 1; row < listAllRow.size(); row++) {
            List<String> aRow = listAllRow.get(row);
            String[] array = new String[aRow.size() - 1];
            for (int col = 1; col < aRow.size(); col++) {
                array[col - 1] = aRow.get(col);
            }
            matrix[row - 1] = array;
        }
        for (String[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
        return matrix;
    }

    private static List<List<String>> buildList(String fullPath) {
        BufferedReader br = IOUtil.createBufferedReader(fullPath, null);
        String line = null;
        List<List<String>> listAllRow = new ArrayList<List<String>>();
        try {
            while ((line = br.readLine()) != null) {
                listAllRow.add(CommUtil.split(line, "\t"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IOUtil.closeReader(br);
        return listAllRow;
    }

    public static void main(String[] args) {
        twoDim();
        String fullPath = IOUtil
                .getJarStayFolder_nologinfo(MultiDimensionArray.class)
                + "/crw.txt";
        construct_2D_Array(fullPath);
    }
}
