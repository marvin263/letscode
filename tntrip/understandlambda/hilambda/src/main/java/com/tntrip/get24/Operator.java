package com.tntrip.get24;

public enum Operator {
    ADD() {
        @Override
        public int calc(int n1, int n2) {
            if (n1 > n2) {
                return Integer.MIN_VALUE;
            }
            return n1 + n2;
        }

        @Override
        public String readable() {
            return "+";
        }
    },
    SUB() {
        @Override
        public int calc(int n1, int n2) {
            return n1 - n2;
        }

        @Override
        public String readable() {
            return "-";
        }
    },
    MULTI() {
        @Override
        public int calc(int n1, int n2) {
            if (n1 > n2) {
                return Integer.MIN_VALUE;
            }
            return n1 * n2;
        }

        @Override
        public String readable() {
            return "*";
        }
    },
    DIV() {
        @Override
        public int calc(int n1, int n2) {
            if (n2 == 0) {
                return Integer.MIN_VALUE;
            }
            if (n1 % n2 != 0) {
                return Integer.MIN_VALUE;
            }
            return n1 / n2;
        }

        @Override
        public String readable() {
            return "/";
        }
    };

    public abstract int calc(int n1, int n2);

    public abstract String readable();

}
