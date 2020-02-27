package com.tntrip.mob.askq;

import com.tntrip.mob.askq.biz.pojo.Pojo;
import com.tntrip.mob.askq.web.filter.CheckLoginFilter;
import com.tntrip.mob.askq.web.interceptor.LogAddressInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@SpringBootApplication
//@EnableAutoConfiguration(exclude = {
//        MybatisAutoConfiguration.class,
//        //DataSourceAutoConfiguration.class
//})
public class HelloSpringBootApplication extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(HelloSpringBootApplication.class, args);
    }

    @Bean
    public Pojo pojoInSpringBootApplication() {
        return new Pojo();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //按照如下的设置顺序通过Filter
        //用户信息Filter
        registry.addInterceptor(new LogAddressInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CheckLoginFilter());
        return registrationBean;
    }

}
