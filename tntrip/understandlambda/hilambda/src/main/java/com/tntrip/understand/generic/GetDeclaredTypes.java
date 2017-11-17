package com.tntrip.understand.generic;

import java.lang.reflect.Field;
import java.time.Month;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nuc on 2016/6/10.
 */
public class GetDeclaredTypes {
    private static EnumSet<TimeUnit> staticSet = EnumSet.allOf(TimeUnit.class);
    private EnumSet<Month> instanceSet = EnumSet.allOf(Month.class);
    private List<? extends Number> list = null;

    public static void printField(Field field) {
        System.out.println(field + "-->\ndeclared type is: " + field.getGenericType());
    }

    public static void main(String[] args) throws Exception {
//        Field staticSet = GetDeclaredTypes.class.getDeclaredField("staticSet");
//        Field instanceSet = GetDeclaredTypes.class.getDeclaredField("instanceSet");
//
//        printField(staticSet);
//        printField(instanceSet);

        Field[] declaredFields = GetDeclaredTypes.class.getDeclaredFields();
        for (Field df : declaredFields) {
            printField(df);
        }
    }
}
