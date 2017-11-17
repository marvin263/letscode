package com.tntrip.understand.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by libing2 on 2016/5/31.
 */
public class MyObject {
    public static void main(String[] args) {
        List rawList = new ArrayList();
        List<Object> objList = new ArrayList<>();
        List<String> strList = new ArrayList<>();

        unsafeAdd_Raw(strList, new Integer(42));

        rawList = objList;
        rawList = strList;

        //objList = strList;
        //unsafeAdd_Object(strList, new Integer(42));

    }

    private static void unsafeAdd_Object(List<Object> objects, Object obj) {

    }

    private static void unsafeAdd_Raw(List rawList, Object obj) {

    }

    private static void wildcardSet(Set<?> set1, Set<?> set2) {
        for (Object o : set1) {

        }
    }
}
