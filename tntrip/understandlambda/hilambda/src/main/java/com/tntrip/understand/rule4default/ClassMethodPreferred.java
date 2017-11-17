package com.tntrip.understand.rule4default;

/**
 * Created by nuc on 2015/11/17.
 */
interface C {
    default void m() {
        System.out.println("C.m");
    }
}

class Concrete {
    public void m() {
        System.out.println("Concrete.m");
    }
}


public class ClassMethodPreferred extends Concrete implements C {
    // class中 同样签名的方法（即使是abstract的），总是优于 接口的default method
    // Hence the default keyword: default methods are a FALLBACK if the class hierarchy doesn't say anything
    public static void main(String[] args) {
        C c = new ClassMethodPreferred();
        c.m();
    }
}
