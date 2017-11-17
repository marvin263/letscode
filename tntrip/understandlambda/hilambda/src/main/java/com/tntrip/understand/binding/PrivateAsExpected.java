package com.tntrip.understand.binding;

/**
 * Created by libing2 on 2015/11/23.
 */
public class PrivateAsExpected {
    static class Superclass {
        private void interesting() {
            System.out.println("Superclass");
        }

        void exampleMethod() {
            interesting();
        }
    }

    static class Sub extends Superclass {
        private void interesting() {
            System.out.println("Sub");
        }
    }

    public static void main(String[] args) {
        Sub me = new Sub();
        me.exampleMethod(); // 很好，符合预期，就是 Superclass
        ((Superclass) me).exampleMethod(); // 很好，符合预期，就是 Superclass
    }
}
