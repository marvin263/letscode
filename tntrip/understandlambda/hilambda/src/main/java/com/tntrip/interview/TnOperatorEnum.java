package com.tntrip.interview;

import java.util.HashMap;
import java.util.Map;


public enum TnOperatorEnum {

    ADD("+", Const.OPERAND_COUNT_2, Const.PRIORITY_1) {
        @Override
        public double compute(double[] args) {
            return args[0] + args[1];
        }
    },
    SUBTRACT("-", Const.OPERAND_COUNT_2, Const.PRIORITY_1) {
        @Override
        public double compute(double[] args) {
            return args[0] - args[1];
        }
    },
    MULTIPLY("*", Const.OPERAND_COUNT_2, Const.PRIORITY_1) {
        @Override
        public double compute(double[] args) {
            return args[0] * args[1];
        }
    },
    DIVIDE("/", Const.OPERAND_COUNT_2, Const.PRIORITY_1) {
        @Override
        public double compute(double[] args) {
            return args[0] / args[1];
        }
    },
    POW("^", Const.OPERAND_COUNT_2, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.pow(args[0], args[1]);
        }
    },
    YROOT("~", Const.OPERAND_COUNT_2, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.pow(args[0], 1.0d / args[1]);
        }

    },
    LOG("log", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log10(args[0]);
        }
    },
    LOG1P("log1p", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log10(1 + args[0]);
        }
    },
    LOG2P("log2p", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log10(2 + args[0]);
        }
    },
    LN("ln", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log(args[0]);
        }
    },
    LN1P("ln1p", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log(1 + args[0]);
        }
    },
    LN2P("ln2p", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.log(2 + args[0]);
        }
    },
    SQUARE("square", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return args[0] * args[0];
        }
    },
    SQRT("sqrt", Const.OPERAND_COUNT_1, Const.PRIORITY_3) {
        @Override
        public double compute(double[] args) {
            return Math.sqrt(args[0]);
        }
    };
    private final String alias;
    private final int operandCount;
    private final int priority;
    private static final Map<String, TnOperatorEnum> mapOperators = initializeMap();

    private static Map<String, TnOperatorEnum> initializeMap() {
        TnOperatorEnum[] values = TnOperatorEnum.values();
        Map<String, TnOperatorEnum> mapOperators = new HashMap<>();
        for (TnOperatorEnum opr : values) {
            mapOperators.put(opr.alias, opr);
            mapOperators.put(opr.name().toLowerCase(), opr);
        }
        return mapOperators;
    }

    public static TnOperatorEnum fromString(String str) {
        if (mapOperators.containsKey(str.toLowerCase())) {
            return mapOperators.get(str.toLowerCase());
        }
        throw new RuntimeException("No TnOperatorEnum for string=" + str);
    }

    TnOperatorEnum(final String alias, final int operandCount, final int priority) {
        this.priority = priority;
        this.alias = alias;
        this.operandCount = operandCount;
    }

    public int operandCount() {
        return operandCount;
    }

    public int priority() {
        return priority;
    }

    public abstract double compute(double[] args);

    /**
     * 当前操作符的优先级更高
     *
     * @param other
     * @return
     */
    public boolean higherPriority(TnOperatorEnum other) {
        return this.priority() - other.priority() > 0;
    }
}

class Const {
    static final int PRIORITY_1 = 1;
    static final int PRIORITY_2 = 2;
    static final int PRIORITY_3 = 3;

    static final int OPERAND_COUNT_1 = 1;
    static final int OPERAND_COUNT_2 = 2;
}
