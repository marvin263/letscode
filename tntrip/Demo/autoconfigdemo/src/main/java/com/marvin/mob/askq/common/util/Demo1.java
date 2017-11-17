package com.marvin.mob.askq.common.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Created by libing2 on 2016/11/8.
 */
@Component
@ConditionalOnClass(value = Demo1.class)
public class Demo1 {
}
