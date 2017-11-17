package com.tntrip.understand.generic;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nuc on 2016/6/11.
 */
public class Declared_Dynamic_Actual<E> {
    private List<Integer> second = new ArrayList<Integer>();

    public static void kindsOfType() throws Exception {
        Field field = Declared_Dynamic_Actual.class.getDeclaredField("second");
        System.out.println("------------getGenericType------------");
        AnalyzeType.analyzeType(field.getGenericType());

        Type gettype = field.getType();
        System.out.println("------------getType------------");
        AnalyzeType.analyzeType(gettype);

        System.out.println("------------getClass------------");
        Class<? extends List> aClass = new Declared_Dynamic_Actual().second.getClass();
        AnalyzeType.analyzeType(aClass);
    }

    public static void collections_empty_list() throws Exception {
        // public static final List EMPTY_LIST = new EmptyList<>();
        // class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable
        Field field = Collections.class.getDeclaredField("EMPTY_LIST");
        System.out.println("------------getGenericType------------");
        AnalyzeType.analyzeType(field.getGenericType());

        Type gettype = field.getType();
        System.out.println("------------getType------------");
        AnalyzeType.analyzeType(gettype);

        System.out.println("------------getClass------------");
        Class<?> aClass = field.get(Collections.emptyList()).getClass();
        AnalyzeType.analyzeType(aClass);

    }


    public static void main(String[] args) throws Exception {
        kindsOfType();
        System.out.println("\n");
        collections_empty_list();
    }
}
