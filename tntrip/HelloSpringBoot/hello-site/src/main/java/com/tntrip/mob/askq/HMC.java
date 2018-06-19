package com.tntrip.mob.askq;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * Created by nuc on 2017/6/29.
 */
//@Configuration
public class HMC {
    @Bean
    public HttpMessageConverters fdf() {
        HttpMessageConverter hmc = new AbstractHttpMessageConverter() {
            @Override
            protected boolean supports(Class clazz) {
                return true;
            }

            @Override
            protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
                return null;
            }

            @Override
            protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
                outputMessage.getBody().write(o.getClass().getName().getBytes("utf-8"));
            }
        };
        System.out.println("HMC.hmc");
        return new HttpMessageConverters(hmc);
    }
}
