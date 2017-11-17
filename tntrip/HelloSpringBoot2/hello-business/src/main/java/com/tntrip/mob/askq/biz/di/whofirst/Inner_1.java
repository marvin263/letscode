package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class Inner_1 {
    @Autowired
    private Inner_2 inner2;

    public Inner_1() {
        System.out.println("Inner_1's default constructor");
    }
}
