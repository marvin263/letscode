package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class Inner_3 {
    @Autowired
    private Inner_4 inner4;
    public Inner_3() {
        System.out.println("Inner_3's default constructor");
    }
}
