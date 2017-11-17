package com.tntrip.understand.targettyping;

import java.util.concurrent.Callable;

/**
 * Created by nuc on 2015/11/20.
 */
public class CondExprPassDown {
    public static void passDownTargetType() {
        // 条件表达式，可以将 外围上下文的 target type 传递到 后面的 表达式
        Callable<Integer> c =
                (System.currentTimeMillis() % 2 == 0) ?
                        () -> 23 :
                        () -> 42;

        try {
            System.out.println(c.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        passDownTargetType();
    }
}
