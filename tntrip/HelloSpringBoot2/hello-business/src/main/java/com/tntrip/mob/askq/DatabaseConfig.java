/**
 * Copyright (C) 2006-2015 tntrip All rights reserved
 */
package com.tntrip.mob.askq;


import com.tntrip.mob.askq.biz.di.service.Party;
import com.tntrip.mob.askq.biz.di.service.impl.Person;
import com.tntrip.mob.askq.biz.pojo.OnlyOnePojo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = AskqBeanConfig.class)
public class DatabaseConfig {
    @Bean(name = "xyz")
    public OnlyOnePojo createOnlyOnePojo() {
        return new OnlyOnePojo();
    }

    @Bean("ppp1")
    public Party parrty1() {
        return new Person();
    }

    @Bean("ppp2")
    public Party parrty2() {
        return new Person();
    }

    @Bean("ppp3")
    public Party parrty3() {
        return new Person();
    }

    @Bean("ppp4")
    public Party parrty4() {
        return new Person();
    }

    @Bean("ppp5")
    public Party parrty5(OnlyOnePojo xyz) {
        System.out.println(xyz);
        return new Person();
    }

}
