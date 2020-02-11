package com.tntrip.get24;

public abstract class Formula {
    final int n1, n2, n3, n4;
    final Operator op1, op2, op3;

    Formula(int[] nums, Operator[] operators) {
        this.n1 = nums[0];
        this.n2 = nums[1];
        this.n3 = nums[2];
        this.n4 = nums[3];
        this.op1 = operators[0];
        this.op2 = operators[1];
        this.op3 = operators[2];

    }

    public final boolean findResult(int rst) {
        return rst == 24;
    }

    public final boolean shouldDiscard(int rst) {
        return rst == Integer.MIN_VALUE;
    }

    public abstract int calc();

    public abstract String readable();
}
