package com.tntrip.docases;

import java.util.Arrays;

/**
 * @Author libing2
 * @Date 2021/6/9
 */
public class TestCopyOf {
    public static void main(String[] args) {
        int[] a = new int[]{1, 2, 3};
        int[] b = Arrays.copyOf(a, a.length);
        b[0] = 100;

        // 1. a[0] == ????
        System.out.println(a[0]);
        // 2. b[0] == 100
        System.out.println(b[0]);


        int[][] aa = new int[][]{
                new int[]{11, 22, 33},
                new int[]{111, 222, 333},
        };
        int[][] deepCopy = Sudoku.deepCopy(aa);

        int[][] bb = Arrays.copyOf(aa, aa.length);
        bb[0][0] = 1111;

        // 3. aa[0][0] == ????
        System.out.println(aa[0][0]);
        // 4. bb[0][0] == 1111
        System.out.println(bb[0][0]);
        System.out.println(Arrays.deepToString(deepCopy));
    }
}
