package com.tntrip.mob.askq.biz.di.whofirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class Autter {
    @Autowired
    private ZInner inner;

    // 先 new 出来 Outter；
    // 然后，再去 new Inner
    public Autter() {
        System.out.println("Autter's default constructor");
    }
}
