package com.tntrip.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libing2 on 2016/10/12.
 */
public class PECS {
    public static void main(String[] args) {
        List<?> list1 = new ArrayList<>();
//        list1.add(new Object());//1. OK?

        List<? extends Number> list2 = new ArrayList<>();
        Number n = list2.get(0);//2. OK?
//        list2.add(new Integer(0));//3. OK?


        List<? super Number> list3 = new ArrayList<>();
//        Number num = list3.get(0);//4. OK?
        list3.add(new Integer(0));//5. OK?
    }
}
