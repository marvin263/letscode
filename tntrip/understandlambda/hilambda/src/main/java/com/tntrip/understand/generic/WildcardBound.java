package com.tntrip.understand.generic;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by libing2 on 2016/6/8.
 */
public class WildcardBound {

    public static <T extends AbstractList & Comparable<? super T> & Cloneable & Comparator<R>,
            V extends Date,
            R>
    T max(Collection<? extends T> coll, V v) {
        Iterator<? extends T> i = coll.iterator();
        T candidate = i.next();

        while (i.hasNext()) {
            T next = i.next();
            if (next.compareTo(candidate) > 0)
                candidate = next;
        }
        return candidate;
    }

    public static void printBound() throws Exception {
        Method method = WildcardBound.class.getMethod("max", Collection.class, Date.class);
        TypeVariable<Method>[] tps = method.getTypeParameters();
        System.out.println("Method's type parameter" + Arrays.asList(tps));

        // T extends AbstractList & Comparable<? super T> & Cloneable & Comparator<R>
        System.out.println("bounds: " + Arrays.asList(tps[0].getBounds()));

        //返回值是：Comparable<? super T>。。。。这种带有 <> 才是 ParameterizedType
        ParameterizedType bound = (ParameterizedType) (tps[0].getBounds()[3]);
        // 只有 ParameterizedType 才存在 ActualTypeArgument 一说
        System.out.println("Actual Type Arguments: " + Arrays.asList(bound.getActualTypeArguments()));
        //返回值是：? super T。。。。ParameterizedType的尖括号里面的值
        WildcardType wildcard = (WildcardType) (bound.getActualTypeArguments()[0]);

        System.out.println("WILDCARD: " + wildcard);

        Type[] lowerBounds = wildcard.getLowerBounds();
        System.out.println("WILDCARD lower bound: " + Arrays.asList(lowerBounds));//T

        Type[] upperBounds = wildcard.getUpperBounds();
        System.out.println("WILDCARD upper bound: " + Arrays.asList(upperBounds));

    }

    public static void main(String[] args) throws Exception {
        printBound();
    }
}



























