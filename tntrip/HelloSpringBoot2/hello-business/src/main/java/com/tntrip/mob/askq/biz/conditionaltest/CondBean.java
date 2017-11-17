package com.tntrip.mob.askq.biz.conditionaltest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.stereotype.Component;

/**
 * Created by libing2 on 2016/11/8.
 */

@Component
@ConditionalOnJava(range = ConditionalOnJava.Range.EQUAL_OR_NEWER,
        value = ConditionalOnJava.JavaVersion.NINE)
public class CondBean {
}
