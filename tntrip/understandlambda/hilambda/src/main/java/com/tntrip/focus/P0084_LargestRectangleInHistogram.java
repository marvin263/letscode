package com.tntrip.focus;

import java.util.ArrayList;
import java.util.List;

public class P0084_LargestRectangleInHistogram {
    public static class OneColumn {
        public final int idx;
        public int h;

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
        int max = 0;
        int idx = 0;

        while (true) {
            while (idx <= heights.length - 1 && heights[idx] <= 0) {
                idx++;
            }
            // idx 是第一个 >0 的列
            if (idx <= heights.length - 1) {
                list.add(OneColumn.create(idx, heights[idx]));
            } else {
                break;
            }

            int from = idx;
            for (++idx; (idx <= heights.length - 1 && heights[idx] > 0); idx++) {
                int h = heights[idx];
                int highest = list.get(list.size() - 1).h;
                // 更高 或 相等 的列
                if (h >= highest) {
                    list.add(OneColumn.create(idx, h));
                }
                // 更矮了
                else {
                    for (int i = list.size() - 1; i >= 0; i--) {
                        OneColumn oc = list.get(i);
                        if (oc.h > h) {
                            if (i == 0) {
                                int curArea = oc.h * (idx - from);
                                max = Math.max(curArea, max);
                            } else {
                                int curArea = oc.h * (idx - oc.idx);
                                max = Math.max(curArea, max);
                            }
                            oc.h = h;
                        } else {
                            break;
                        }
                    }
                    list.add(OneColumn.create(idx, h));
                }
            } // 结束时，到了最有一个元素，或到了 列高度为 <=0 的元素
            int lastIdx = idx;
            for (int i = list.size() - 1; i > 0; i--) {
                OneColumn oc = list.get(i);
                int curArea = oc.h * (lastIdx - oc.idx);
                max = Math.max(curArea, max);
            }

            max = Math.max(list.get(list.size() - 1).h * (idx - from), max);
        }
        return max;
    }


    public static void main(String[] args) {
        P0084_LargestRectangleInHistogram p = new P0084_LargestRectangleInHistogram();

        System.out.println(p.largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));//10
        System.out.println(p.largestRectangleArea(new int[]{3, 6, 5, 7, 4, 8, 1, 0}));//20
    }
}
