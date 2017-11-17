package com.tntrip.understand.bridgemethod;

import com.tntrip.understand.util.ReflectUtils;

/**
 * Created by nuc on 2015/11/20.
 */
public class Bridge {
    interface A<T> {
        void m(T t);
    }

    // B 从 A 继承了方法 m(String t)
    // 这点是必须的，得符合程序员预期
    interface B extends A<String> {
    }

    static class AImpl<T> implements A<T> {
        @Override
        public void m(T t) {
            System.out.println("I'm AImpl");
        }
    }

    static class BImpl implements B {
        @Override
        public void m(String t) {
            System.out.println("I'm BImpl");
        }
    }

    public static void main(String[] args) {
        B b = new BImpl();
        ReflectUtils.printMembers(b);

//        b.m("b");
//        //b.m(new Date());
//        A a = b;
//        a.m(new Date());
    }
}
