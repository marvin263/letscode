package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class Inner_4 {
    @Autowired
    private AInner_5 inner5;
    public Inner_4() {
        System.out.println("Inner_4's default constructor");
    }
}
