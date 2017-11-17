package com.tntrip.understand.bridgemethod;

/**
 * Created by nuc on 2015/11/21.
 */
public class StubbornBridge {
    static class Superclass<T extends Bound> {
        public void m1(T arg) {
        }

        public T m2() {
            return null;
        }
    }

    static class AnotherSubclass extends Superclass<SubTypeOfBound> {

    }

    public static void main(String[] args) {
        AnotherSubclass as = new AnotherSubclass();

    }

}
