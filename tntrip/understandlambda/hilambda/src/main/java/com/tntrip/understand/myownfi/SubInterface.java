package com.tntrip.understand.myownfi;

import java.util.Date;

/**
 * Created by nuc on 2015/12/5.
 */
interface SuperInterface {
    public default String apply(Date date, Integer i) {
        return date.toString() + i;
    }
}

@FunctionalInterface
public interface SubInterface extends SuperInterface {
    String apply(Date date, Integer i);
}

class SuperImpl implements SuperInterface {

}

class SubImpl implements SubInterface {
    @Override
    public String apply(Date date, Integer i) {
        return null;
    }
}

class TestIt {
    public static void main(String[] args) {
        SubInterface sub = (date, i) -> date.toString() + (i + 1);
        System.out.println(sub.apply(new Date(), 10));

        ((SuperInterface) sub).apply(new Date(), 100);

//        ThreeParamFunction<String, Integer, Date, String> tpFunc =
//                (str, i, date) -> str + "--" + (i + 10) + "--" + date.toString();
//        String str = tpFunc.apply("Hello", 36, new Date());
//        System.out.println(str);
    }
}
