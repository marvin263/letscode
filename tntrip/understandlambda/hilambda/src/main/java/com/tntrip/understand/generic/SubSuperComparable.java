package com.tntrip.understand.generic;

/**
 * Created by libing2 on 2016/7/10.
 */
public class SubSuperComparable {
    public static class A0 implements Comparable<A0> {
        @Override
        public int compareTo(A0 o) {
            return 0;
        }
    }

//    public static class A1 extends A0 implements Comparable<A1> {
//        @Override
//        public int compareTo(A1 o) {
//            return 0;
//        }
//    }
}
