package com.tntrip.focus;

import java.util.ArrayList;
import java.util.List;

public class P0084_LargestRectangleInHistogram {
    public static class OneColumn {
        public int idx;
        public final int h;

        public static OneColumn create(int idx, int h) {
            return new OneColumn(idx, h);
        }

        private OneColumn(int idx, int h) {
            this.idx = idx;
            this.h = h;
        }
    }

    public int largestRectangleArea(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }
        List<OneColumn> list = new ArrayList<>();
        OneColumn oc = OneColumn.create(0, heights[0]);
        list.add(oc);

        int max = heights[0];
        for (int i = 1; i < heights.length; i++) {
            int h = heights[i];
            int highestByFar = list.get(list.size() - 1).h;
            if (h > highestByFar) {
                list.add(OneColumn.create(i, h));
            } else {
                max = addShorterColumn(max, list, OneColumn.create(i, h));
            }
        }

        int idx = heights.length;
        for (int i = list.size() - 1; i >= 0; i--) {
            OneColumn existed = list.get(i);
            max = Math.max(max, existed.h * (idx - existed.idx));
        }
        return max;
    }

    private int addShorterColumn(int prevMax, List<OneColumn> list, OneColumn oc) {
        int curIdx = oc.idx;
        list.add(oc);
        int max = prevMax;

        for (int i = list.size() - 2; i >= 0; i--) {
            OneColumn existed = list.get(i);
            if (existed.h == oc.h) {
                list.remove(i);
                oc.idx = existed.idx;
            } else if (existed.h > oc.h) {
                max = Math.max(max, existed.h * (curIdx - existed.idx));
                list.remove(i);
                oc.idx = existed.idx;
            } else {
                break;
            }
        }
        return max;
    }


    public static void main(String[] args) {
        P0084_LargestRectangleInHistogram p = new P0084_LargestRectangleInHistogram();

        System.out.println(p.largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));//10
        System.out.println(p.largestRectangleArea(new int[]{3, 6, 5, 7, 4, 8, 1, 0}));//20
    }
}
