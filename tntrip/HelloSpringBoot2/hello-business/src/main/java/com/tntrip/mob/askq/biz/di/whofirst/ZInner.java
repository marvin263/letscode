package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class ZInner {
    @Autowired
    private Inner_1 inner1;

    public ZInner() {
         System.out.println("ZInner's default constructor");
    }
}
