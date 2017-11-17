package com.tntrip.mob.askq.biz.di.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by libing2 on 2016/10/20.
 */
@Component
public class BF {
    @Autowired
    private AF af;

    public String display() {
        return af.getClass().toString();
    }

}
