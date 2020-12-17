package com.tntrip.focus.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P1200_MinimumAbsoluteDifference {
    public List<List<Integer>> minimumAbsDifference(int[] arr) {
        Arrays.sort(arr);
        int min = arr[1] - arr[0];
        List<List<Integer>> list = new ArrayList<>(arr.length);
        list.add(Arrays.asList(arr[0], arr[1]));

        for (int i = 1; i < arr.length - 1; i++) {
            int diff = arr[i + 1] - arr[i];
            if (diff == min) {
                list.add(Arrays.asList(arr[i], arr[i + 1]));
            } else if (diff < min) {
                min = diff;
                list.clear();
                list.add(Arrays.asList(arr[i], arr[i + 1]));
            } else {

            }
        }
        return list;
    }

    public static void main(String[] args) {
        P1200_MinimumAbsoluteDifference p = new P1200_MinimumAbsoluteDifference();
        // [[1,2],[2,3],[3,4]]
        int[] arr1 = new int[]{4, 2, 1, 3};
        System.out.println(p.minimumAbsDifference(arr1));

        // [[1,3]]
        int[] arr2 = new int[]{1, 3, 6, 10, 15};
        System.out.println(p.minimumAbsDifference(arr2));

        // [[26,27]]
        int[] arr3 = new int[]{40,11,26,27,-20};
        System.out.println(p.minimumAbsDifference(arr3));
    }
}
