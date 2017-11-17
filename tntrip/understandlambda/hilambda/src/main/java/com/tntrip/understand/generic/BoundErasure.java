package com.tntrip.understand.generic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by libing2 on 2016/5/31.
 */
public class BoundErasure {
    public static <T extends MyObject> void doSomething(T t) {
    }

    public static <E> Set<E> union(Set<? extends E> s1,
                                   Set<? extends E> s2) {
        Set<E> rst = new HashSet<>(s1);
        for (E e : s2) {
            rst.add(e);
        }
        return rst;
    }

    public static void main(String[] args) {
        Set<Integer> integers = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Double> doubles = new HashSet<>(Arrays.asList(1.1D, 2.2D, 3.3D));

        Set<Number> numbers = union(integers, doubles);
        System.out.println(numbers);
    }
}
