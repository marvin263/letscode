package com.tntrip.understand.library;


import java.awt.*;
import java.util.List;

/**
 * Created by nuc on 2015/12/6.
 */
public class TnShape {
    private Color color;
    private double area;

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "TnShape{" +
                "color=" + color +
                '}';
    }

    public static double totalArea(List<TnShape> shapeList) {
        double total = 0.0D;
        for (TnShape s : shapeList) {
            if (s.getColor() == Color.red) {
                total += s.getArea();
            }
        }


        double totalOtherStyle = shapeList.stream().
                filter(s -> s.getColor() == Color.RED).
                mapToDouble(TnShape::getArea).
                sum();


        return total;
    }
}
