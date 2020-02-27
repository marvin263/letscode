package com.tntrip.mob.askq;

public class HiMarvin {
    public static class B {
        private A0 a0;

        public B(A0 a0) {
            this.a0 = a0;
            System.out.println(this.a0);
        }
    }

    public static class A0 {
        private B b;

        public A0() {
            b = new B(this);
            System.out.println("In A0, this=" + this);
        }

        @Override
        public String toString() {
            return "A0";
        }
    }

    public static class A1 extends A0 {
        public A1() {
            super();
            System.out.println("In A1, this=" + this);
        }

        @Override
        public String toString() {
            return "A1";
        }
    }

    public static class A2 extends A1 {
        public A2() {
            super();
            System.out.println("In A2, this=" + this);
        }

        @Override
        public String toString() {
            return "A2";
        }
    }

//    public static void main(String[] args) {
//        new A2();
//    }
}
