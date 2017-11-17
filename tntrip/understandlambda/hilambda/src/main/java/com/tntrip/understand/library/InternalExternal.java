package com.tntrip.understand.library;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 2015/12/6.
 */
public class InternalExternal {
    // 传统方式--外部迭代
    private static double external(List<TnShape> shapes) {
        double max = 0.0D;
        //所有 红色的shape中，最大面积是多少
        for (TnShape s : shapes) {
            if (s.getColor() == Color.RED) {
                if (s.getArea() > max) {
                    max = s.getArea();
                }
            }
        }
        return max;
    }

    // 内部迭代
    private static double internal(List<TnShape> shapes) {
        double max = shapes.stream().
                filter(s -> s.getColor() == Color.RED).//只要红色
                mapToDouble(TnShape::getArea).//转换成 double
                max().orElse(0.0D);// 获取最大值
        return max;
    }

    public static void main(String[] args) {
        List<TnShape> shapes = new ArrayList<>();
        external(shapes);
        internal(shapes);
    }
}
