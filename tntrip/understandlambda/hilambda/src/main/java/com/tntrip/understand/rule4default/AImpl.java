package com.tntrip.understand.rule4default;

/**
 * Created by nuc on 2015/11/17.
 */
interface A0 {
    default void m() {
        System.out.println("A0.m");
    }
}

interface A1 extends A0 {
    default void m() {
        System.out.println("A1.m");
    }
}

public class AImpl implements A0, A1 {
    public static void main(String[] args) {
        A0 a = new AImpl();
        // 那些 <b>被</b> override 的方法，将被 抛弃
        // A0.m 被 override 过了，是被 A1.m override 的。所以，A0.m 被抛弃了
        a.m();
    }
}
