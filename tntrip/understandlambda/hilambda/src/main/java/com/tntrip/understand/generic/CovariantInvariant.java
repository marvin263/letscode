package com.tntrip.understand.generic;

import java.util.Date;

/**
 * Created by libing2 on 2016/7/10.
 */
public class CovariantInvariant {
    public static void main(String[] args) {
        Long[] longArray = new Long[10];
        // 由于 array 的 covariant 特性
        // Long 是 Object子类-->Long[] 是 Object[] 子类
        Object[] objArrayActualLongArray = longArray;
        objArrayActualLongArray[0] = new Object();//但是，错误推迟到了运行期
        varargsAndGeneric("", new Date(), new Date(), new Date());
    }

    @SafeVarargs
    public static <T> void varargsAndGeneric(String s, T... t) {
        for (T t1 : t) {
            System.out.println(s + t1);
        }
    }
}
