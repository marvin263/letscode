package com.tntrip.understand.binding;

/**
 * Created by libing2 on 2015/11/23.
 */
class Binding {
    public static void staticBinding() {
        System.out.println("Binding.staticBinding");
    }
}

// there is no dynamic dispatch on static methods
public class StaticLateBinding extends Binding {
    public static void staticBinding() {
        System.out.println("StaticLateBinding.staticBinding");
    }

    public static void main(String[] args) {
        StaticLateBinding slb = new StaticLateBinding();
        // 静态方法，强制转型为 Binding。则，编译期就决定了调用哪一个方法
        ((Binding) slb).staticBinding();
        ((StaticLateBinding) slb).staticBinding();

        ((Binding) null).staticBinding();
        ((StaticLateBinding) null).staticBinding();
    }
}
