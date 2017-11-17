package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class Inner_2 {
    @Autowired
    private Inner_3 inner3;
    public Inner_2() {
        System.out.println("Inner_2's default constructor");
    }
}
