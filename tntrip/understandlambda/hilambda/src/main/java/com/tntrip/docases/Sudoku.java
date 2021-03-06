package com.tntrip.docases;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @Author libin
 * @Date 2021/5/20
 */
public class Sudoku {
    public static final int[][] NINE_VALUES_1 = new int[][]{
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

    public static final int[][] NINE_VALUES_55 = new int[][]{
            new int[]{1, 0, 0, 8, 0, 0, 4, 0, 6},
            new int[]{0, 9, 6, 0, 0, 0, 0, 1, 0},
            new int[]{3, 0, 0, 1, 0, 2, 0, 9, 0},

            new int[]{0, 0, 9, 0, 8, 0, 5, 0, 1},
            new int[]{0, 0, 0, 3, 0, 5, 0, 0, 0},
            new int[]{5, 0, 8, 0, 1, 0, 6, 0, 0},

            new int[]{0, 8, 0, 7, 0, 9, 0, 0, 5},
            new int[]{0, 6, 0, 0, 0, 0, 1, 7, 0},
            new int[]{2, 0, 7, 0, 0, 1, 0, 0, 9},

    };

    public static final int[][] NINE_VALUES_56 = new int[][]{
            new int[]{0, 0, 4, 0, 3, 5, 0, 2, 0},
            new int[]{0, 0, 0, 8, 0, 0, 4, 0, 1},
            new int[]{5, 0, 9, 0, 1, 0, 0, 8, 0},

            new int[]{0, 9, 0, 0, 0, 1, 0, 0, 3},
            new int[]{2, 0, 3, 0, 0, 0, 1, 0, 8},
            new int[]{7, 0, 0, 3, 0, 0, 0, 4, 0},

            new int[]{0, 5, 0, 0, 4, 0, 8, 0, 2},
            new int[]{9, 0, 2, 0, 0, 8, 0, 0, 0},
            new int[]{0, 4, 0, 1, 2, 0, 7, 0, 0},
    };

    public static final int[][] NINE_VALUES_99 = new int[][]{
            new int[]{0, 0, 5, 0, 0, 8, 0, 0, 9},
            new int[]{0, 0, 0, 0, 0, 0, 4, 2, 8},
            new int[]{0, 0, 6, 0, 0, 1, 0, 0, 0},

            new int[]{0, 0, 0, 0, 2, 0, 1, 0, 7},
            new int[]{0, 3, 0, 0, 0, 0, 0, 5, 0},
            new int[]{9, 0, 4, 0, 5, 0, 0, 0, 0},

            new int[]{0, 0, 0, 7, 0, 0, 8, 0, 0},
            new int[]{1, 2, 9, 0, 0, 0, 0, 0, 0},
            new int[]{5, 0, 0, 9, 0, 0, 2, 0, 0}
    };

    public static final int[][] SIX_VALUES = new int[][]{
            new int[]{0, 6, 4, 2, 3, 0},
            new int[]{0, 2, 0, 0, 1, 0},
            new int[]{2, 0, 6, 3, 0, 1},

            new int[]{3, 0, 5, 6, 0, 2},
            new int[]{0, 3, 0, 0, 2, 0},
            new int[]{4, 5, 0, 0, 6, 3},
    };

    public static final int[][] SIX_VALUES_1 = new int[][]{
            new int[]{1, 0, 5, 2, 0, 4},
            new int[]{3, 0, 0, 0, 5, 6},
            new int[]{5, 4, 0, 0, 0, 3},

            new int[]{0, 0, 0, 0, 0, 0},
            new int[]{0, 0, 0, 0, 0, 0},
            new int[]{0, 0, 0, 0, 0, 0}
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
        TreeSet<Integer> tryCandidates;
        int[][] orgnValues = null;

        public static Trying createTrying(int r, int c, int candidateIndex, int[][] orgnValues, TreeSet<Integer> tryCandidates) {
            Trying t = new Trying();
            t.r = r;
            t.c = c;
            t.tryCandidates = tryCandidates;
            t.candidateIndex = candidateIndex;
            t.orgnValues = orgnValues;
            return t;
        }

        private boolean hasTheCandidate() {
            return candidateIndex <= tryCandidates.size() - 1;
        }

        private int getTheCandidate() {
            if (!hasTheCandidate()) {
                throw new RuntimeException(String.format("no candidate exists for: t=%s", this));
            }

            int idx = 0;
            for (Integer v : tryCandidates) {
                if (idx == candidateIndex) {
                    return v;
                }
                idx++;
            }
            throw new RuntimeException("aaaaaaa");
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
        TreeMap<Integer, TreeSet<Integer>> existed4Row;
        /**
         * 任意一个给定列，其上所已存在的数字
         */
        TreeMap<Integer, TreeSet<Integer>> existed4Col;
        /**
         * 任意一个给rowIndex, colIndex，该点所在的square内存在的所有数字
         */
        TreeSet<Integer>[][] existedWithinSquare;

        /**
         * 任意一个给rowIndex, colIndex，该点所允许的候选值
         */
        TreeSet<Integer>[][] candidates;

        boolean explicitDead = false;

        public Situation(int[][] values) {
            this.suku = SudokuEnum.int2Sudu(values.length);
            this.values = values;

            TreeMap<Integer, TreeSet<Integer>>[] rc = createRowCol();
            this.existed4Row = rc[0];
            this.existed4Col = rc[1];

            this.existedWithinSquare = createSquare();
            this.candidates = createCandidates();

            fillAllDeterminateValue();
            System.out.println("Constructor generates:");
            System.out.println(values2Str());
        }

        private TreeMap<Integer, TreeSet<Integer>>[] createRowCol() {
            TreeMap<Integer, TreeSet<Integer>>[] rc = new TreeMap[]{
                    new TreeMap<Integer, TreeSet<Integer>>(),
                    new TreeMap<Integer, TreeSet<Integer>>()
            };

            TreeMap<Integer, TreeSet<Integer>> existed4Row = rc[0];
            TreeMap<Integer, TreeSet<Integer>> existed4Col = rc[1];
            for (int r = 0; r < suku.width; r++) {
                existed4Row.put(r, new TreeSet<>());
                for (int c = 0; c < suku.width; c++) {
                    existed4Col.put(c, new TreeSet<>());
                }
            }
            return rc;
        }

        private TreeSet<Integer>[][] createSquare() {
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
                boolean alreadyDead = fillRowColSquare();
                if (alreadyDead) {
                    explicitDead = true;
                    break;
                }
                fillCandidates();
                found = fillDeterminateValue();
            } while (found);

            if (!finishSituation() && !deadSituation()) {
                int[] rc = findLeastCandidateCell();
                this.t = Trying.createTrying(rc[0], rc[1], 0, values, new TreeSet<>(candidates[rc[0]][rc[1]]));
            }
        }

        /**
         * true-->没有冲突
         * <p>
         * false-->冲突啦。explicitDead
         *
         * @return
         */
        private boolean fillRowColSquare() {
            clearRowColSquare();
            for (int r = 0; r < values.length; r++) {
                int[] row = values[r];
                for (int c = 0; c < row.length; c++) {
                    int v = values[r][c];
                    if (v == 0) {
                        continue;
                    }
                    if (this.existed4Row.get(r).contains(v)
                            || this.existed4Col.get(c).contains(v)
                            || this.existedWithinSquare[r][c].contains(v)) {
                        return true;
                    }
                    this.existed4Row.get(r).add(v);
                    this.existed4Col.get(c).add(v);
                    this.existedWithinSquare[r][c].add(v);
                }
            }
            return false;
        }

        private void clearRowColSquare() {
            this.existed4Row.forEach((key, value) -> value.clear());
            this.existed4Col.forEach((key, value) -> value.clear());
            Arrays.stream(existedWithinSquare).forEach(e -> Arrays.stream(e).forEach(TreeSet::clear));
        }

        private void clearCandidate() {
            Arrays.stream(this.candidates).forEach(e -> Arrays.stream(e).forEach(TreeSet::clear));
        }

        /**
         * 给定rowIndex, colIndex，其可选值
         *
         * @return
         */
        private void fillCandidates() {
            // 清除掉每一个cell的candidate
            clearCandidate();
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

        /**
         * 存在 (尚未填值) 的cell -->尚未done
         * <p>
         * 否则done
         * <p>
         * 尚未完成
         *
         * @return
         */
        private boolean finishSituation() {
            if (explicitDead) {
                return false;
            }
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
            if (explicitDead) {
                return true;
            }
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
            if (s.finishSituation()) {
                System.out.println("Finally found!!!");
                System.out.println(s.values2Str());
                return;
            }
            // 2. 当前死局
            if (s.deadSituation()) {
                System.out.println("dead situation, pop");
                System.out.println(s.values2Str());
                stack.pop();
                continue;
            }
            // 3. 当前挺好，继续向下钻取
            if (s.t != null && s.t.hasTheCandidate()) {
                int[][] newValues = deepCopy(s.t.orgnValues);
                newValues[s.t.r][s.t.c] = s.t.getTheCandidate();
                s.t.candidateIndex = s.t.candidateIndex + 1;

                Situation nextSituation = new Situation(newValues);
                stack.push(nextSituation);
            } else {
                System.out.println("no candidate, pop");
                System.out.println(s.values2Str());
                stack.pop();
            }

        }
    }

    public static <T> T deepCopy(T src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Sudoku sudu = new Sudoku();

//        sudu.letsFind(new Situation(FOUR_VALUES));
//        sudu.letsFind(new Situation(SIX_VALUES));
//
//        sudu.letsFind(new Situation(NINE_VALUES_1));
//        sudu.letsFind(new Situation(NINE_VALUES_2));
//        sudu.letsFind(new Situation(NINE_VALUES_3));
//        sudu.letsFind(new Situation(NINE_VALUES_4));
//        sudu.letsFind(new Situation(NINE_VALUES_55));
//        sudu.letsFind(new Situation(NINE_VALUES_56));
//        sudu.letsFind(new Situation(SIX_VALUES_1));
        sudu.letsFind(new Situation(NINE_VALUES_99));
    }

}
