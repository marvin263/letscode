package com.tntrip.understand.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 2016/6/15.
 */
public class QuestionMark {

    public <E extends Number> void m1(E e) {
        List listOfRawTypes = new ArrayList();
        System.out.println(listOfRawTypes.size());
    }

    public void m3(List<? extends Number> list) {
    }

    public void m2() {
    }

}
