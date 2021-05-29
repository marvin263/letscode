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

    // 0号：当前已确定的值，已经确定则为1，尚未确定则为0
    // 1--9：是否已允许，如果允许则为1，不允许为0
    // 排序当前的情况：可能性越少，则位于最前面；可能性越多则排最后
    // 尝试时，尝试的是 哪个位置，用谁来尝试的，记录下来
    // 从情况最少的开始尝试
    public static void main(String[] args) {


    }

    public static class RemainedChances {
        public static final RemainedChances RC = new RemainedChances();
        int chances = Integer.MIN_VALUE;
        int rowIndex = 0;
        int colIndex = 0;

        void reset() {
            this.chances = Integer.MIN_VALUE;
            this.rowIndex = 0;
            this.colIndex = 0;
        }
    }

    public static final int WIDTH = 9;
    public static final int SQUARE_W = 3;
    public static final int SQUARE_H = 3;

    public static void initializeSituation(short[][] values) {
        short[][] situation = new short[values.length][values[0].length];
        for (int rowIndex = 0; rowIndex < situation.length; rowIndex++) {
            short[] row = situation[rowIndex];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {

            }
        }


    }

    public static class ExistedNum {
        TreeMap<Integer, TreeSet<Integer>> existed4Row = new TreeMap<>();
        TreeMap<Integer, TreeSet<Integer>> existed4Col = new TreeMap<>();
        TreeSet<Integer>[][] existedWithinSquare;

        public ExistedNum(SudokuEnum suku) {
            existedWithinSquare = new TreeSet[suku.width][suku.width];
            for (int r = 0; r < existedWithinSquare.length; r++) {
                TreeSet<Integer>[] rows = existedWithinSquare[r];
                for (int c = 0; c < rows.length; c++) {
                    SquareBound bound = suku.getBound(r, c);
                    if (existedWithinSquare[bound.sid.r_leftTop][bound.sid.c_leftTop] == null) {
                        existedWithinSquare[bound.sid.r_leftTop][bound.sid.c_leftTop] = new TreeSet<>();
                    }
                    existedWithinSquare[r][c] = existedWithinSquare[bound.sid.r_leftTop][bound.sid.c_leftTop];
                }
            }
        }
    }

    public void ddddddd(SudokuEnum suku, int[][] values) {
        ExistedNum en = new ExistedNum(suku);
        for (int r = 0; r < values.length; r++) {
            int[] row = values[r];
            if (!en.existed4Row.containsKey(r)) {
                en.existed4Row.put(r, new TreeSet<>());
            }
            for (int c = 0; c < row.length; c++) {
                if (!en.existed4Col.containsKey(c)) {
                    en.existed4Col.put(c, new TreeSet<>());
                }
                addExistedValue(en.existed4Col.get(c), values[r][c]);
                addExistedValue(en.existed4Row.get(r), values[r][c]);

                SquareBound bd = suku.getBound(r, c);
                if (r == bd.sid.r_leftTop && c == bd.sid.c_leftTop) {
                    TreeSet<Integer> existed = existed(suku, values, r, c);
                    en.existedWithinSquare[r][c].addAll(existed);
                }
            }
        }
    }

    private static void addExistedValue(TreeSet<Integer> set, int value) {
        if (value != 0) {
            set.add(value);
        }
    }

    public static class SquareBound {
        int r_leftTop;
        int c_leftTop;
        int r_left_inclusive;
        int r_right_inclusive;
        int c_top_inclusive;
        int c_bottom_inclusive;

        public SquareBound(int r_left_inclusive, int r_right_inclusive, int c_top_inclusive, int c_bottom_inclusive, int r_leftTop, int c_leftTop) {
            this.r_left_inclusive = r_left_inclusive;
            this.r_right_inclusive = r_right_inclusive;
            this.c_top_inclusive = c_top_inclusive;
            this.c_bottom_inclusive = c_bottom_inclusive;
            this.r_leftTop = r_leftTop;
            this.c_leftTop = c_leftTop;
        }

    }

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
            SquareBound bd = new SquareBound(
                    this.squareW * quotient_w,
                    (this.squareW * (quotient_w + 1)) - 1,
                    this.squareH * quotient_h,
                    (this.squareH * (quotient_h + 1)) - 1,
                    r_leftTop,
                    c_leftTop
            );
            bounds[rowIndex][colIndex] = bd;
            return bd;
        }
    }

    public static TreeSet<Integer> existed(SudokuEnum suku, int[][] values, int rowIndex, int colIndex) {
        TreeSet<Integer> existed = new TreeSet<>();
        SquareBound bd = suku.getBound(rowIndex, colIndex);
        for (int r = bd.r_left_inclusive; r <= bd.r_right_inclusive; r++) {
            for (int c = bd.c_top_inclusive; c <= bd.c_bottom_inclusive; c++) {
                addExistedValue(existed, values[r][c]);
            }
        }
        return existed;
    }

    public static class EachElement {
        short[][] situation = new short[9][9];
        // 正在尝试哪一个行
        int tryingRow;
        // 正在尝试哪一个列
        int tryingCol;
        // 尝试时使用的是什么值
        int tryingVal;

        //如果撤退回来了，则说明尝试失败了
        public RemainedChances tryWho() {
            RemainedChances.RC.reset();
            for (int rowIndex = 0; rowIndex < situation.length; rowIndex++) {
                short[] row = situation[rowIndex];
                for (int colIndex = 0; colIndex < row.length; colIndex++) {
                    short cellValue = row[colIndex];
                    int chances = remainedChances(cellValue);
                    // 该cell具有更少的可能性
                    // 最终找到的，chances最少，且，位于行列的最前面
                    if (chances > 0 && chances < RemainedChances.RC.chances) {
                        RemainedChances.RC.chances = chances;
                        RemainedChances.RC.rowIndex = rowIndex;
                        RemainedChances.RC.colIndex = colIndex;
                    }
                }
            }
            return RemainedChances.RC;
        }

        public RemainedChances tryValue(int tryingRow, int tryingCol) {
            // 得到新的situation，设置某个值
            short[][] newSituation = this.situation;
            int usedNum = 0;
            for (int i = 1; i < mask.length; i++) {
                // 这就是尝试的第一个值
                if ((newSituation[tryingRow][tryingCol] & mask[i]) == 1) {
                    usedNum = i;
                    break;
                }
            }
            // assert下


            return RemainedChances.RC;
        }

        // 整个行上，不能为该值了
        public void changeTheRow(short[][] newSituation, int usedNum, int tryingRow, int tryingCol) {
            short[] row = newSituation[tryingRow];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {

            }
        }


        // 修改局面
        public RemainedChances modifySituation(int tryingRow, int tryingCol) {
            // 得到新的situation，设置某个值
            short[][] newSituation = this.situation;
            int usedNum = 0;
            for (int i = 1; i < mask.length; i++) {
                // 这就是尝试的第一个值
                if ((newSituation[tryingRow][tryingCol] & mask[i]) == 1) {
                    usedNum = i;
                    break;
                }
            }
            // assert下

            for (int rowIndex = 0; rowIndex < newSituation.length; rowIndex++) {
                short[] row = newSituation[rowIndex];
                for (int colIndex = 0; colIndex < row.length; colIndex++) {
                    short cellValue = row[colIndex];
                    int chances = remainedChances(cellValue);
                    // 该cell具有更少的可能性
                    // 最终找到的，chances最少，且，位于行列的最前面
                    if (chances > 0 && chances < RemainedChances.RC.chances) {
                        RemainedChances.RC.chances = chances;
                        RemainedChances.RC.rowIndex = rowIndex;
                        RemainedChances.RC.colIndex = colIndex;
                    }
                }
            }
            return RemainedChances.RC;
        }


        // 给定的值，还有多少种可能性  
        private int remainedChances(short cellValue) {
            // 已经确定了
            if ((cellValue & mask[0]) == 1) {
                return 0;
            }
            int chances = 0;
            for (int i = 1; i < mask.length; i++) {
                // 这个位置是1，说明还存在可能性
                // 不可能是该值，则此时会为0
                if (((cellValue & mask[i]) == 1)) {
                    chances++;
                }
            }
            return chances;
        }
    }

}
