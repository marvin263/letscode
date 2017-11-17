package com.tntrip.mob.askq.biz.di.circular;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by libing2 on 2016/10/20.
 */

@Component

public class A implements InitializingBean {
    private B b;

    public A() {
        System.out.println("No paramter");
    }

    public A(B b, B b1) {
        System.out.println("2 parameters");
    }

    @Autowired
    public A(B b2, B b, B b3) {
        System.out.println("3 parameters");
    }

    public A(B b) {
        System.out.println("1 parameters");
        this.b = b;
        System.out.println("A is fully constructed. Already injected b into a...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("So good, I'm afterPropertiesSet");
    }

    @PostConstruct
    public void IamCustomizedInitMethod(){
        System.out.println("call me after: 'So good, I'm afterPropertiesSet'");
    }
}
