package com.tntrip.get24;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Get24 {
    private static List<Operator[]> LIST_OPERATORS = fillOperators();

    private static List<Operator[]> fillOperators() {
        List<Operator[]> list = new ArrayList<>();
        Operator[] operators = Operator.values();
        for (Operator op1 : operators) {
            for (Operator op2 : operators) {
                for (Operator op3 : operators) {
                    Operator[] ops = new Operator[3];
                    ops[0] = op1;
                    ops[1] = op2;
                    ops[2] = op3;
                    list.add(ops);
                }
            }
        }
        return list;
    }

    private List<int[]> permutation(int[] nums) {
        List<List<Integer>> permutations = new ArrayList<>();

        for (int n : nums) {
            permutations = addOne(permutations, n);
        }

        List<int[]> collect = permutations.stream().map(e -> {
            int[] s = new int[e.size()];
            for (int i = 0; i < e.size(); i++) {
                s[i] = e.get(i);
            }
            return s;
        }).collect(Collectors.toList());

        return collect;
    }

    private List<List<Integer>> addOne(List<List<Integer>> existed, int n) {
        List<List<Integer>> rst = new ArrayList<>();
        if (existed.isEmpty()) {
            rst.add(new LinkedList<>());
            rst.get(0).add(n);
            return rst;
        }

        for (List<Integer> aPerm : existed) {
            int size = aPerm.size();
            for (int i = 0; i <= size; i++) {
                LinkedList<Integer> aCopy = new LinkedList<>(aPerm);
                rst.add(aCopy);
                aCopy.add(i, n);
            }
        }
        return rst;
    }

    public Set<String> findAllFormulas(int[] nums) {
        long begin = System.currentTimeMillis();
        Set<String> allFormulas = new HashSet<>();

        List<int[]> permutation = permutation(nums);

        for (int[] eachPerm : permutation) {
            for (Operator[] eachOperators : LIST_OPERATORS) {
                Formula[] formulas = new Formula[]{
                        new Formula1(eachPerm, eachOperators),
                        new Formula2(eachPerm, eachOperators),
                        new Formula3(eachPerm, eachOperators),
                        new Formula4(eachPerm, eachOperators),
                        new Formula5(eachPerm, eachOperators)
                };
                for (Formula f : formulas) {
                    if (f.findResult(f.calc())) {
                        allFormulas.add(f.readable());
                    }
                }
            }
        }
        System.out.println("Cost " + (System.currentTimeMillis() - begin) + "ms");
        return allFormulas;
    }

    public static void main(String[] args) {
        Get24 dd = new Get24();
        int[] nums = {1, 2, 3, 4};
        dd.findAllFormulas(nums);
    }
}
