package com.tntrip.understand.histream;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nuc on 2015/12/7.
 */
public class UnderstandStream {

    public static int sum(List<Integer> list) {
        return 0;
    }

    public static void non_interference() {
        List<String> list = new ArrayList<>(Arrays.asList("One", "Two"));
        Stream<String> s = list.stream();
        list.add("Three");

        String str = s.collect(Collectors.joining(" "));

        System.out.println(str);
    }


    public static void stateful_bad() {
        List<Integer> listSource = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 4));

        Set<Integer> iseeyou = Collections.synchronizedSet(new HashSet<>());

        List<Integer> listResult = listSource.stream().
                map(e -> {
                    if (iseeyou.contains(e)) {
                        return e;
                    } else {
                        iseeyou.add(e);
                        return 0;
                    }
                }).
                collect(Collectors.toList());
        //线程同时启动，去查看是否包含。就全是0啦

        //用于生产 listResult 的，在返回值（不要和 iseeyou 搞混了）
        System.out.println(listResult);

    }

    public static void eliminate_side_effect(List<String> listSource) {
        //ptn可以用在多线程中
        Pattern ptn = Pattern.compile("\\d+");

        List<String> rst_bad = new ArrayList<>();
        listSource.stream().
                filter(s -> ptn.matcher(s).matches())
                .forEach(s -> rst_bad.add(s));

        List<String> rst_good = listSource.stream()
                .filter(s -> ptn.matcher(s).matches())
                .collect(Collectors.toList());
    }

    public static void experienceCollector() {
        Collector<Employee, ?, Integer> c = Collectors.summingInt(Employee::getSalary);

        Collector<Employee, ?, Map<Employee.Department, Integer>> c2 =
                Collectors.groupingBy(Employee::getDept, c);
    }


    public static void main(String[] args) {
        stateful_bad();
    }
}
