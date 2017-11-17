package com.tntrip.understand.rule4default;

/**
 * Created by nuc on 2015/11/17.
 */
interface X {
    default void m() {
        System.out.println("X.m");
    }
}

interface Y {
    default void m() {
        System.out.println("Y.m");
    }
}

public class XYImpl implements X, Y {
    // XYImpl 去掉 m() 方法，则编译错误
    // 实现类必须要再次实现下 重复的default方法，因为 XYImpl 糊涂了，它不知道 X Y中谁更合适
    // 为 default 而专门增强的 super语法，可以让你选择 某个特定 父接口中的 默认实现
    @Override
    public void m() {
        Y.super.m();
    }

    public static void main(String[] args) {
        X x = new XYImpl();
        x.m();
    }
}
