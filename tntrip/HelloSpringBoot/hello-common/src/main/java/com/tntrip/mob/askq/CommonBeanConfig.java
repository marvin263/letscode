package com.tntrip.mob.askq;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.tntrip")
public class CommonBeanConfig {
    @Bean("pet1")
    public Pet createPet() {
        return new LovelyPet();
    }

    @Bean("marvinBeanPostProcessor")
    public BeanPostProcessor createCustomizedBPP() {
        BeanPostProcessor bpp = new BeanPostProcessor() {
            // simply return the instantiated bean as-is
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                System.out.println(beanName);
                return bean;
            }

            //在创建bean后输出bean的信息
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
        return bpp;
    }
    @Bean
    public BeanFactoryPostProcessor bfPostProcessor(){
        BeanFactoryPostProcessor bfPostProcessor = new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                System.out.println(beanFactory);
            }
        };
        return bfPostProcessor;
    }

}
