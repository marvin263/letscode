package com.tntrip.understand.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libing2 on 2016/7/10.
 */
public class Swap {
    public static <E> void swap_E(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

    public static void swap_QuestionMark(List<?> list, int i, int j) {
        // list.set(i, list.set(j, list.get(i))); // 除了null, List<?> 放不进任何元素

        swapHelper(list, i, j);

    }

    // Private helper method for wildcard capture
    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));

        List<E> listE = new ArrayList<>();
        List<?> listAny = new ArrayList<>();

        listAny = listE;

        // listE = listAny;


    }

}
