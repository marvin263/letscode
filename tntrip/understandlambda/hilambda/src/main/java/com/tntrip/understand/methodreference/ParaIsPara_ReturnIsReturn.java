package com.tntrip.understand.methodreference;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by nuc on 2015/11/20.
 */
public class ParaIsPara_ReturnIsReturn {
    public static int characterCountDifference(String str1, String str2) {
        return str1.length() - str2.length();
    }

    public static void existingMethodAsLambdaExpression() {
        Comparator<String> c = Comparator.comparing(str -> str.length());
        // 入参就是 str，返回值就是 length()的返回值
        c = Comparator.comparing(String::length);

        // 特定对象的 实例方法
        Set<String> knownNames = new HashSet<>();
        Predicate<String> p = knownNames::contains;
        //lambda表达式就是一个 无名方法
        //右边就是一个 implicitly-typed lambda。（lambda的入参就是 这个方法的入参；lambda的返回值就是 这个方法的返回值）

        Function<String, Integer> func1 = str -> str.hashCode(); //这不是 method reference

        BiFunction<String, String, Integer> bf1 = (bigStr, str) -> bigStr.indexOf(str); //这不是 method reference

        // FI第一个参数.后面的方法名(FI第二个参数, FI第三个参数...)
        // “后面的方法名”的返回值，就是 FI需要的返回值
        BiFunction<String, String, Integer> bf2 = String::indexOf;
        BiFunction<String, String, Integer> bf3 = ParaIsPara_ReturnIsReturn::characterCountDifference;
        Function<String, Integer> func2 = String::hashCode;

    }

    public void getFirstOccurence(String context, String occurrence) {

        Integer i = context.indexOf(occurrence);

        BiFunction<String, String, Integer> func1 = (str, occur) -> str.indexOf(occur);
        Integer i1 = func1.apply(context, occurrence);


        BiFunction<String, String, Integer> func2 = String::indexOf;
        Integer i2 = func2.apply(context, occurrence);

    }

}
