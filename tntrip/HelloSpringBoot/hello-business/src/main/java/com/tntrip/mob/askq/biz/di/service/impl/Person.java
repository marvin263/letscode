package com.tntrip.mob.askq.biz.di.service.impl;

import com.tntrip.mob.askq.biz.di.service.Party;
import org.springframework.stereotype.Component;

/**
 * Created by libing2 on 2016/10/16.
 */
@Component(value = "psn")
public class Person implements Party {
    @Override
    public String partyName() {
        return "Person";
    }
}
