package com.tntrip.focus;


import java.util.HashSet;
import java.util.Set;

public class P0217_ContainsDuplicate {
    public boolean containsDuplicate(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return false;
        }

        Set<Integer> set = new HashSet<>(nums.length);
        for (int n : nums) {
            set.add(n);
        }
        return set.size() != nums.length;

    }
}

