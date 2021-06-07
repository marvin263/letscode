package com.tntrip.docases;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author libin
 * @Date 2021/5/20
 */
public class Sudoku {
    public static final AtomicLong global = new AtomicLong();
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

    public static final int[][] NINE_VALUES = new int[][]{
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

    public static final int[][] NINE_VALUES_2 = new int[][]{
            new int[]{0, 9, 0, 8, 0, 0, 0, 3, 0},
            new int[]{0, 0, 2, 0, 0, 0, 7, 0, 6},
            new int[]{0, 0, 0, 3, 5, 2, 0, 0, 0},

            new int[]{0, 0, 8, 0, 0, 0, 6, 0, 0},
            new int[]{2, 0, 0, 1, 0, 9, 0, 0, 3},
            new int[]{0, 0, 7, 0, 0, 0, 1, 0, 0},

            new int[]{0, 0, 0, 4, 2, 6, 0, 0, 0},
            new int[]{5, 0, 3, 0, 0, 0, 9, 0, 0},
            new int[]{0, 4, 0, 0, 0, 3, 0, 8, 0},

    };

    public static final int[][] NINE_VALUES_3 = new int[][]{
            new int[]{0, 0, 0, 0, 1, 0, 5, 0, 0},
            new int[]{0, 0, 0, 0, 3, 4, 0, 0, 0},
            new int[]{3, 0, 9, 0, 0, 0, 0, 0, 4},

            new int[]{0, 6, 3, 0, 0, 0, 0, 5, 0},
            new int[]{0, 4, 5, 0, 0, 0, 0, 6, 7},
            new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0},

            new int[]{5, 0, 0, 0, 6, 1, 9, 0, 0},
            new int[]{0, 0, 0, 0, 4, 2, 0, 0, 0},
            new int[]{8, 0, 6, 0, 0, 0, 7, 0, 0},

    };

    public static final int[][] NINE_VALUES_4 = new int[][]{
            new int[]{6, 7, 8, 6, 1, 0, 5, 8, 3},
            new int[]{6, 5, 8, 9, 3, 4, 2, 9, 9},
            new int[]{3, 1, 9, 2, 5, 8, 6, 7, 4},
            
            new int[]{2, 6, 3, 7, 8, 9, 4, 5, 1},
            new int[]{9, 4, 5, 1, 2, 3, 8, 6, 7},
            new int[]{1, 8, 7, 4, 0, 0, 2, 3, 9},
            
            new int[]{5, 2, 4, 3, 6, 1, 9, 8, 0},
            new int[]{7, 9, 1, 8, 4, 2, 3, 0, 0},
            new int[]{8, 3, 6, 5, 9, 0, 7, 0, 2},

    };

    public static final int[][] SIX_VALUES = new int[][]{
            new int[]{0, 6, 4, 2, 3, 0},
            new int[]{0, 2, 0, 0, 1, 0},
            new int[]{2, 0, 6, 3, 0, 1},

            new int[]{3, 0, 5, 6, 0, 2},
            new int[]{0, 3, 0, 0, 2, 0},
            new int[]{4, 5, 0, 0, 6, 3},
    };

    public static final int[][] FOUR_VALUES = new int[][]{
            new int[]{0, 4, 2, 0},
            new int[]{2, 0, 0, 0},
            new int[]{4, 0, 0, 0},
            new int[]{0, 0, 0, 3}

    };

    public enum SudokuEnum {
        NINE(9, 3, 3),
        SIX(6, 3, 2),
        FOUR(4, 2, 2);
        public final int width;
        public final int squareW;
        public final int squareH;
        private final SquareBound[][] bounds;
        private static Map<Integer, SudokuEnum> map = create();

        private static Map<Integer, SudokuEnum> create() {
            Map<Integer, SudokuEnum> map = new HashMap<>();
            for (SudokuEnum anEnum : values()) {
                map.put(anEnum.width, anEnum);
            }
            return map;
        }

        public static SudokuEnum int2Sudu(int width) {
            return map.get(width);
        }

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

            int quotient_w = colIndex / this.squareW;
            int quotient_h = rowIndex / this.squareH;
            int r_leftTop = this.squareH * quotient_h;
            int c_leftTop = this.squareW * quotient_w;
            bounds[rowIndex][colIndex] = new SquareBound(
                    this.squareH * quotient_h,
                    (this.squareH * (quotient_h + 1)) - 1,
                    this.squareW * quotient_w,
                    (this.squareW * (quotient_w + 1)) - 1,
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

    public static class Trying {
        int r = -1;
        int c = -1;
        int candidateIndex = -1;
        int[][] orgnValues = null;

        public static Trying createTrying(int r, int c, int candidateIndex, int[][] orgnValues) {
            Trying t = new Trying();
            t.r = r;
            t.c = c;
            t.candidateIndex = candidateIndex;
            t.orgnValues = orgnValues;
            return t;
        }

        @Override
        public String toString() {
            return "Trying{" +
                    "r=" + r +
                    ", c=" + c +
                    ", candidateIndex=" + candidateIndex +
                    '}';
        }
    }

    /**
     * 给定所有值后，可选情况
     */
    public static class Situation {
        final int[][] values;
        final SudokuEnum suku;
        /**
         * 当前正在测试
         */
        Trying t;

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

        public Situation(int[][] values) {
            this.suku = SudokuEnum.int2Sudu(values.length);
            this.values = values;
            this.existedWithinSquare = createExistedWithinSquare(suku);
            this.candidates = createCandidates();
            fillAllDeterminateValue();
            System.out.println("Constructor generates:");
            System.out.println(values2Str());
        }

        /**
         * 如果具有唯一值，则填充起来。
         * <p>
         * true-->可以继续；false-->某个cell已经无法填充了，得返回
         *
         * @return
         */
        private void fillAllDeterminateValue() {
            boolean found = false;
            do {
                fillRowColSquare();
                calcCandidates();
//                if (deadSituation()) {
//                    break;
//                }
                found = fillDeterminateValue();
            } while (found);

            if (!doneSituation() && !deadSituation()) {
                int[] rc = findLeastCandidateCell();
                this.t = Trying.createTrying(rc[0], rc[1], 0, values);
            }
        }


        private boolean hasTheCandidate() {
            if (t == null) {
                return false;
            }
            return t.candidateIndex <= candidates[t.r][t.c].size() - 1;
        }

        private int getTheCandidate() {
            if (!hasTheCandidate()) {
                throw new RuntimeException(String.format("no candidate exists for: t=%s", t.toString()));
            }

            int idx = 0;
            for (Integer v : candidates[t.r][t.c]) {
                if (idx == t.candidateIndex) {
                    return v;
                }
                idx++;
            }
            throw new RuntimeException("aaaaaaa");
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


        private TreeSet<Integer>[][] createCandidates() {
            TreeSet<Integer>[][] treeSets = new TreeSet[this.suku.width][this.suku.width];
            for (int r = 0; r < treeSets.length; r++) {
                TreeSet<Integer>[] row = treeSets[r];
                for (int c = 0; c < row.length; c++) {
                    treeSets[r][c] = new TreeSet<>();
                }
            }
            return treeSets;
        }

        private void fillRowColSquare() {
            this.existed4Row.clear();
            this.existed4Col.clear();
            // 清除掉每一个cell的小方形候选值
            Arrays.stream(existedWithinSquare).forEach(row -> Arrays.stream(row).forEach(TreeSet::clear));

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

                    SquareBound bd = this.suku.getBound(r, c);
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
         * @return
         */
        private void calcCandidates() {
            // 清除掉每一个cell的candidate
            Arrays.stream(candidates).forEach(row -> Arrays.stream(row).forEach(TreeSet::clear));

            for (int eachValue = 1; eachValue <= suku.width; eachValue++) {
                for (int r = 0; r < suku.width; r++) {
                    if (this.existed4Row.get(r).contains(eachValue)) {
                        continue;
                    }
                    for (int c = 0; c < suku.width; c++) {
                        if (fixedCell(r, c)) {
                            continue;
                        }
                        if (this.existed4Col.get(c).contains(eachValue)) {
                            continue;
                        }
                        if (this.existedWithinSquare[r][c].contains(eachValue)) {
                            continue;
                        }
                        //行、列、square 三者都不含有该元素，于是：该元素是可选值
                        this.candidates[r][c].add(eachValue);
                    }
                }
            }
        }

        private boolean fixedCell(int rowIndex, int colIndex) {
            return values[rowIndex][colIndex] != 0;
        }

        public String values2Str() {
            StringBuilder sb = new StringBuilder();
            for (int[] row : values) {
                sb.append(Arrays.toString(row));
                sb.append("\r\n");
            }
            return sb.toString();
        }

        /**
         * 存在 (尚未填值) 的cell -->尚未done
         * <p>
         * 否则done
         * <p>
         * 尚未完成
         *
         * @return
         */
        private boolean doneSituation() {
            for (int r = 0; r < suku.width; r++) {
                for (int c = 0; c < suku.width; c++) {
                    // 尚未设置，未结束
                    if (!fixedCell(r, c)) {
                        return false;
                    }
                }
            }
            return true;
        }


        /**
         * 存在 (尚未填值 && 候选值==0) 的cell
         * <p>
         * 死局
         *
         * @return
         */
        private boolean deadSituation() {
            for (int r = 0; r < candidates.length; r++) {
                TreeSet<Integer>[] row = candidates[r];
                for (int c = 0; c < row.length; c++) {
                    // 尚未填充。且，完全不好使，该cell没有任何可用的值
                    if (!fixedCell(r, c) && candidates[r][c].size() == 0) {
                        String info = String.format("[%d, %d] has NO candidate!!!", r, c);
                        System.out.println(info);
                        System.out.println(values2Str());
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean fillDeterminateValue() {
            for (int r = 0; r < candidates.length; r++) {
                TreeSet<Integer>[] row = candidates[r];
                for (int c = 0; c < row.length; c++) {
                    // 尚未填充。且，该出仅有一个候选值，填上它
                    if (!fixedCell(r, c) && candidates[r][c].size() == 1) {
                        values[r][c] = candidates[r][c].stream().findFirst().get();
                        String info = String.format("[%d, %d] has determinate candidate %d.", r, c, values[r][c]);
                        System.out.println(info);
                        System.out.println(values2Str());
                        return true;
                    }
                }
            }
            return false;
        }

        private int[] findLeastCandidateCell() {
            int[] rc = new int[]{0, 0};
            int min = Integer.MAX_VALUE;
            for (int r = 0; r < candidates.length; r++) {
                TreeSet<Integer>[] rows = candidates[r];
                for (int c = 0; c < rows.length; c++) {
                    if (fixedCell(r, c)) {
                        continue;
                    }
                    TreeSet<Integer> candidates = rows[c];
                    if (candidates.size() == 0) {
                        throw new RuntimeException("fdfdf");
                    }
                    if (candidates.size() < min) {
                        min = candidates.size();
                        rc[0] = r;
                        rc[1] = c;
                    }
                }
            }
            return rc;
        }
    }

    private final Stack<Situation> stack = new Stack<>();

    private void letsFind(Situation cur) {
        stack.push(cur);
        while (!stack.isEmpty()) {
            Situation s = stack.peek();
            // 1. 找到了
            if (s.doneSituation()) {
                System.out.println("Finally found!!!");
                System.out.println(s.values2Str());
                return;
            }
            // 2. 当前死局
            if (s.deadSituation()) {
                System.out.println("dddddd");
                stack.pop();
                continue;
            }
            // 3. 当前挺好，继续向下钻取
            if (s.hasTheCandidate()) {
                int[][] newValues = Arrays.copyOf(s.t.orgnValues, s.t.orgnValues.length);
                newValues[s.t.r][s.t.c] = s.getTheCandidate();
                s.t.candidateIndex = s.t.candidateIndex + 1;

                Situation nextSituation = new Situation(newValues);
                stack.push(nextSituation);
            } else {
                System.out.println("ffdfdff");
                stack.pop();
            }

        }
    }

    public static void main(String[] args) {
        Sudoku sudu = new Sudoku();

//        sudu.letsFind(new Situation(FOUR_VALUES));
//        sudu.letsFind(new Situation(SIX_VALUES));
//        sudu.letsFind(new Situation(NINE_VALUES_2));

//        sudu.letsFind(new Situation(NINE_VALUES));
        sudu.letsFind(new Situation(NINE_VALUES_3));
//        sudu.letsFind(new Situation(NINE_VALUES_4));


//        Situation s41 = new Situation(SudokuEnum.FOUR, s4.values);
//        System.out.println(s41);
    }

}
