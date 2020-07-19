package com.example.myboot224;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * -XX:+PrintGCDetails -XX:+PrintGCTimeStamps  -XX:+PrintGCDateStamps -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:./logs/gc.log
 * 
 */
@SpringBootApplication
public class TestingWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestingWebApplication.class, args);
    }
}
