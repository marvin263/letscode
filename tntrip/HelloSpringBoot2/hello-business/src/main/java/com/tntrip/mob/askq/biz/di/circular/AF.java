package com.tntrip.mob.askq.biz.di.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by libing2 on 2016/10/20.
 */
@Component
public class AF {
    @Autowired
    private BF bf;

    public String display() {
        return bf.getClass().toString();
    }
}
