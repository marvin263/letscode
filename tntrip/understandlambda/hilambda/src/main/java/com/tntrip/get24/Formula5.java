package com.tntrip.get24;


/**
 * a (b (c d))
 */
public class Formula5 extends Formula {
    public Formula5(int[] nums, Operator[] operators) {
        super(nums, operators);
    }

    public int calc() {
        int rst1 = op3.calc(n3, n4);
        if (shouldDiscard(rst1)) {
            return rst1;
        }

        int rst2 = op2.calc(n2, rst1);
        if (shouldDiscard(rst2)) {
            return rst2;
        }

        int rst3 = op1.calc(n1, rst2);
        if (shouldDiscard(rst3)) {
            return rst3;
        }
        return rst3;
    }

    public String readable() {
        String sb = n1 +
                op1.readable() +
                "(" +
                n2 +
                op2.readable() +
                "(" +
                n3 +
                op3.readable() +
                n4 +
                "))";
        return sb;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{3, 5, 7, 3};
        Operator[] operators = new Operator[]{Operator.ADD, Operator.DIV, Operator.MULTI
        };
        Formula5 f2 = new Formula5(nums, operators);
        System.out.println(f2.calc());
        System.out.println(f2.readable());
    }
}
