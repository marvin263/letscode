package com.tntrip.docases;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @Author libin
 * @Date 2021/5/20
 */
public class Sudoku {
    public static final short n_00_0000_0000 = 0x00F;
    public static final short n2 = 0x02;
    public static final short n3 = 0x03;
    public static final short[] mask = new short[]{
            0x01F,//    1   00_0000_0001
            0x02F,//    2   00_0000_0010
            0x04F,//    4   00_0000_0100
            0x08F,//    8   00_0000_1000
            0x10F,//   16   00_0001_0000
            0x20F,//   32   00_0010_0000
            0x40F,//   64   00_0100_0000
            0x80F,//  128   00_1000_0000
            0x100F,// 256   01_0000_0000
            0x200F,// 512   10_0000_0000
    };
    public static final int[][] VALUES = new int[][]{
            new int[]{0, 6, 0, 7, 0, 0, 0, 0, 0},
            new int[]{0, 0, 2, 0, 0, 0, 8, 0, 3},
            new int[]{0, 0, 0, 0, 1, 0, 7, 0, 6},

            new int[]{6, 4, 0, 5, 0, 0, 0, 2, 0},
            new int[]{5, 0, 0, 9, 0, 4, 0, 0, 8},
            new int[]{0, 9, 0, 0, 0, 6, 0, 4, 1},

            new int[]{7, 0, 3, 0, 8, 0, 0, 0, 0},
            new int[]{2, 0, 9, 0, 0, 0, 1, 0, 0},
            new int[]{0, 0, 0, 0, 0, 2, 0, 3, 0},

    };

    public enum SudokuEnum {
        NINE(9, 3, 3),
        SIX(6, 3, 2),
        FOUR(4, 2, 2),
        ;
        public final int width;
        public final int squareW;
        public final int squareH;
        private final SquareBound[][] bounds;

        SudokuEnum(final int width, final int squareW, final int squareH) {
            this.width = width;
            this.squareW = squareW;
            this.squareH = squareH;
            bounds = new SquareBound[width][width];
        }

        public SquareBound getBound(int rowIndex, int colIndex) {
            if (rowIndex >= this.width || colIndex >= this.width) {
                throw new RuntimeException(String.format("Index must be within the bound. rowIndex=%d, colIndex=%d. Sudoku.width=%d",
                        rowIndex, colIndex, this.width));
            }
            if (bounds[rowIndex][colIndex] != null) {
                return bounds[rowIndex][colIndex];
            }

            int quotient_w = rowIndex / this.squareW;
            int quotient_h = colIndex / this.squareH;
            int r_leftTop = this.squareW * quotient_w;
            int c_leftTop = this.squareH * quotient_h;
            bounds[rowIndex][colIndex] = new SquareBound(
                    this.squareW * quotient_w,
                    (this.squareW * (quotient_w + 1)) - 1,
                    this.squareH * quotient_h,
                    (this.squareH * (quotient_h + 1)) - 1,
                    r_leftTop,
                    c_leftTop
            );
            return bounds[rowIndex][colIndex];
        }
    }

    public static class SquareBound {
        int r_leftTop;
        int c_leftTop;
        int r_left_inclusive;
        int r_right_inclusive;
        int c_top_inclusive;
        int c_bottom_inclusive;

        public SquareBound(int r_left_inclusive, int r_right_inclusive, int c_top_inclusive, int c_bottom_inclusive,
                           int r_leftTop, int c_leftTop) {
            this.r_left_inclusive = r_left_inclusive;
            this.r_right_inclusive = r_right_inclusive;
            this.c_top_inclusive = c_top_inclusive;
            this.c_bottom_inclusive = c_bottom_inclusive;
            this.r_leftTop = r_leftTop;
            this.c_leftTop = c_leftTop;
        }
    }

    /**
     * 给定所有值后，可选情况
     */
    public static class Situation {
        /**
         * 任意一个给定行，其上所已存在的数字
         */
        TreeMap<Integer, TreeSet<Integer>> existed4Row = new TreeMap<>();
        /**
         * 任意一个给定列，其上所已存在的数字
         */
        TreeMap<Integer, TreeSet<Integer>> existed4Col = new TreeMap<>();
        /**
         * 任意一个给rowIndex, colIndex，该点所在的square内存在的所有数字
         */
        TreeSet<Integer>[][] existedWithinSquare;

        /**
         * 任意一个给rowIndex, colIndex，该点所允许的候选值
         */
        TreeSet<Integer>[][] candidates;

        public Situation(SudokuEnum suku, int[][] values) {
            this.existedWithinSquare = createExistedWithinSquare(suku);
            this.candidates = createCandidates(suku);
            fillExisted(suku, values);
            calcCandidates(suku);
        }

        private TreeSet<Integer>[][] createExistedWithinSquare(SudokuEnum suku) {
            TreeSet<Integer>[][] existedWithinSquare = new TreeSet[suku.width][suku.width];
            for (int r = 0; r < existedWithinSquare.length; r++) {
                TreeSet<Integer>[] rows = existedWithinSquare[r];
                for (int c = 0; c < rows.length; c++) {
                    SquareBound bd = suku.getBound(r, c);
                    if (existedWithinSquare[bd.r_leftTop][bd.c_leftTop] == null) {
                        existedWithinSquare[bd.r_leftTop][bd.c_leftTop] = new TreeSet<>();
                    }
                    existedWithinSquare[r][c] = existedWithinSquare[bd.r_leftTop][bd.c_leftTop];
                }
            }
            return existedWithinSquare;
        }


        private TreeSet<Integer>[][] createCandidates(SudokuEnum suku) {
            TreeSet<Integer>[][] treeSets = new TreeSet[suku.width][suku.width];
            for (int r = 0; r < treeSets.length; r++) {
                TreeSet<Integer>[] row = treeSets[r];
                for (int c = 0; c < row.length; c++) {
                    treeSets[r][c] = new TreeSet<Integer>();
                }
            }
            return treeSets;
        }

        private void fillExisted(SudokuEnum suku, int[][] values) {
            for (int r = 0; r < values.length; r++) {
                int[] row = values[r];
                if (!this.existed4Row.containsKey(r)) {
                    this.existed4Row.put(r, new TreeSet<>());
                }
                for (int c = 0; c < row.length; c++) {
                    if (!this.existed4Col.containsKey(c)) {
                        this.existed4Col.put(c, new TreeSet<>());
                    }
                    addExistedValue(this.existed4Col.get(c), values[r][c]);
                    addExistedValue(this.existed4Row.get(r), values[r][c]);

                    SquareBound bd = suku.getBound(r, c);
                    if (r == bd.r_leftTop && c == bd.c_leftTop) {
                        TreeSet<Integer> existed = existed(suku, values, r, c);
                        this.existedWithinSquare[r][c].addAll(existed);
                    }
                }
            }
        }

        private TreeSet<Integer> existed(SudokuEnum suku, int[][] values, int rowIndex, int colIndex) {
            TreeSet<Integer> existed = new TreeSet<>();
            SquareBound bd = suku.getBound(rowIndex, colIndex);
            for (int r = bd.r_left_inclusive; r <= bd.r_right_inclusive; r++) {
                for (int c = bd.c_top_inclusive; c <= bd.c_bottom_inclusive; c++) {
                    addExistedValue(existed, values[r][c]);
                }
            }
            return existed;
        }

        private void addExistedValue(TreeSet<Integer> set, int value) {
            if (value != 0) {
                set.add(value);
            }
        }

        /**
         * 给定rowIndex, colIndex，其可选值
         *
         * @param suku
         * @return
         */
        private void calcCandidates(SudokuEnum suku) {
            for (int v = 1; v <= suku.width - 1; v++) {
                for (int r = 0; r < suku.width; r++) {
                    if (this.existed4Row.get(r).contains(v)) {
                        continue;
                    }
                    for (int c = 0; c < suku.width; c++) {
                        if (this.existed4Col.get(c).contains(v)) {
                            continue;
                        }
                        if (this.existedWithinSquare[r][c].contains(v)) {
                            continue;
                        }
                        //行、列、square 三者都不含有该元素，于是：该元素是可选值
                        this.candidates[r][c].add(v);
                    }
                }
            }
        }
    }


    // 0号：当前已确定的值，已经确定则为1，尚未确定则为0
    // 1--9：是否已允许，如果允许则为1，不允许为0
    // 排序当前的情况：可能性越少，则位于最前面；可能性越多则排最后
    // 尝试时，尝试的是 哪个位置，用谁来尝试的，记录下来
    // 从情况最少的开始尝试
    public static void main(String[] args) {
        Situation s = new Situation(SudokuEnum.NINE, VALUES);
        System.out.println(s);
    }

}
