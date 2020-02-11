package com.tntrip.get24;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class aaaa {
    Get24 g = new Get24();

    private List<int[]> fdf() {
        int[] digits = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<int[]> list = new ArrayList<>();
        for (int i0 = 0; i0 < digits.length; i0++) {
            for (int i1 = 0; i1 < digits.length; i1++) {
                for (int i2 = 0; i2 < digits.length; i2++) {
                    for (int i3 = 0; i3 < digits.length; i3++) {
                        int[] nums = new int[]{digits[i0], digits[i1], digits[i2], digits[i3]};
                        list.add(nums);
                    }
                }
            }
        }

        Set<String> ddddddd = new HashSet<>();
        for (int[] nums : list) {
            Set<String> allFormulas = g.findAllFormulas(nums);
            if (allFormulas.size() == 1) {
                for (String formula : allFormulas) {
                    System.out.println(formula);
                }
                System.out.println("Total: " + allFormulas.size());
                System.out.println();
                ddddddd.addAll(allFormulas);
            }
        }
        for (String s : ddddddd) {
            System.out.println(s);
        }
        System.out.println("Total numbers: " + ddddddd.size());
        return list;
    }

    public static void main(String[] args) {
        aaaa a = new aaaa();
        a.fdf();

    }

}
