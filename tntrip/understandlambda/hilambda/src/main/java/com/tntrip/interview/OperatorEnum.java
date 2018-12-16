package com.tntrip.interview;

import java.util.HashMap;
import java.util.Map;

public enum OperatorEnum {
    ADD("+") {
        @Override
        public int operandCount() {
            return 2;
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public double compute(double[] args) {
            return args[0] + args[1];
        }
    },
    SUBTRACT("-") {
        @Override
        public int operandCount() {
            return 2;
        }

        @Override

        public int priority() {
            return 1;
        }

        @Override
        public double compute(double[] args) {
            return args[0] - args[1];
        }
    },
    MULTIPLY("*") {
        @Override
        public int operandCount() {
            return 2;
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public double compute(double[] args) {
            return args[0] * args[1];
        }
    },
    DIVIDE("/") {
        @Override
        public int operandCount() {
            return 2;
        }

        @Override
        public int priority() {
            return 1;
        }

        @Override
        public double compute(double[] args) {
            return args[0] / args[1];
        }
    },
    POW("^") {
        @Override
        public int operandCount() {
            return 2;
        }

        @Override
        public int priority() {
            return 3;
        }

        @Override
        public double compute(double[] args) {
            return Math.pow(args[0], args[1]);
        }
    },
    YROOT("~") {
        @Override
        public int operandCount() {
            return 2;
        }


        @Override
        public int priority() {
            return 3;
        }

        @Override
        public double compute(double[] args) {
            return Math.pow(args[0], 1.0d / args[1]);
        }

    },
    LOG("log") {
        @Override
        public double compute(double[] args) {
            return Math.log10(args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    LOG1P("log1p") {
        @Override
        public double compute(double[] args) {
            return Math.log10(1 + args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    LOG2P("log2p") {
        @Override
        public double compute(double[] args) {
            return Math.log10(2 + args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    LN("ln") {
        @Override
        public double compute(double[] args) {
            return Math.log(args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    LN1P("ln1p") {
        @Override
        public double compute(double[] args) {
            return Math.log(1 + args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    LN2P("ln2p") {
        @Override
        public double compute(double[] args) {
            return Math.log(2 + args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    SQUARE("square") {
        @Override
        public double compute(double[] args) {
            return args[0] * args[0];
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    },
    SQRT("sqrt") {
        @Override
        public double compute(double[] args) {
            return Math.sqrt(args[0]);
        }

        @Override
        public int operandCount() {
            return 1;
        }

        @Override
        public int priority() {
            return 3;
        }
    };
    private final String alias;
    private static final Map<String, OperatorEnum> mapOperators = initializeMap();

    private static Map<String, OperatorEnum> initializeMap() {
        OperatorEnum[] values = OperatorEnum.values();
        Map<String, OperatorEnum> mapOperators = new HashMap<>();
        for (OperatorEnum opr : values) {
            mapOperators.put(opr.alias, opr);
            mapOperators.put(opr.name().toLowerCase(), opr);
        }
        return mapOperators;
    }

    public static OperatorEnum fromString(String str) {
        if (mapOperators.containsKey(str.toLowerCase())) {
            return mapOperators.get(str.toLowerCase());
        }
        throw new RuntimeException("No OperatorEnum for string=" + str);
    }

    OperatorEnum(final String alias) {
        this.alias = alias;
    }

    public abstract int operandCount();

    public abstract int priority();

    public abstract double compute(double[] args);

    /**
     * 当前操作符的优先级更高
     *
     * @param other
     * @return
     */
    public boolean higherPriority(OperatorEnum other) {
        return this.priority() - other.priority() > 0;
    }
}
