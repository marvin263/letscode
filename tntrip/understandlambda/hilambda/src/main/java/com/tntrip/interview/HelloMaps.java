package com.tntrip.interview;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by libing2 on 2016/10/11.
 */
public class HelloMaps {
    public static void main(String[] args) {
        String[] array = new String[]{"BZ", "A", "BA", "D"};

        TreeMap<String, Integer> treem = new TreeMap<>();
        putValues(array, treem);

        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        putValues(array, linkedHashMap);

        listKeys(treem);//???
        System.out.print("----");
        listKeys(linkedHashMap);//???
    }

    private static void putValues(String[] array, Map<String, Integer> map) {
        for (int i = 0; i < array.length; i++) {
            map.put(array[i], i);
        }
    }

    private static void listKeys(Map<String, Integer> map) {
        int size = map.size();
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (i == size - 1) {
                System.out.print(entry.getKey());
            } else {
                System.out.print(entry.getKey() + ",");
            }
            i++;
        }
    }
}
