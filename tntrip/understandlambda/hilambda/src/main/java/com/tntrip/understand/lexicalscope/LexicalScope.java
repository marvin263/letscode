package com.tntrip.understand.lexicalscope;

import java.util.function.Function;

/**
 * Created by nuc on 2015/11/18.
 */
public class LexicalScope {
    @Override
    public String toString() {
        return "Hello World!";
    }

    private final Runnable lambda = () -> {
        System.out.println(this); // 指的是 enclosing object
        System.out.println(toString()); // 指的是 enclosing object.toString()
    };
    private final Runnable inner = new Runnable() {
        @Override
        public void run() {
            System.out.println(this);
            System.out.println(toString()); // 即，this.toString()
            // 匿名内部类 实现了 Runnable，同时，这是一个类，所以，也继承自 Object，
            // 所以，也有各种 toString(), wait等方法。
            // 所以，上面对 toString()的调用：
            // 其实是 匿名内部类inherit Object's toString() shadow了 enclosing env的toString()
        }
    };

    public void wontShadow() {
        String hw = "Hello World!";
        for (int i = 0; i < 10; i++) {
            // String hw = ""; //Compile error
            System.out.println(hw);
        }

        // 就和 for 块中 不能 shadow enclosing env中的变量一样
        // lambda中（包括lambda的参数）都不能 shadow enclosing env中的变量
        Function<String, Integer> func = (para) -> {
            // String hw = "it's my world!!!"; //Compile error
            System.out.println(para);
            return para.length();
        };
    }

    public static void main(String[] args) {
        new LexicalScope().lambda.run();
        new LexicalScope().inner.run();
        new LexicalScope().wontShadow();
    }
}
