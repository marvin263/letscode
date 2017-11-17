package com.marvin.mob.askq.common.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by libing2 on 2016/11/8.
 */
@Configuration
public class DemoAutoConfiguration {
    @Bean
    public Demo1 demo1() {
        return new Demo1();
    }

    @Bean
    @ConditionalOnMissingBean(value = Demo1.class)
    public Demo2 demo2() {
        return new Demo2();
    }
}
