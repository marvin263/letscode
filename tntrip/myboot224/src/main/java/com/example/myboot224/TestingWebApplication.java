package com.example.myboot224;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * -XX:+PrintGCDetails -XX:+PrintGCTimeStamps  -XX:+PrintGCDateStamps -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:./logs/gc.log
 */
@SpringBootApplication
public class TestingWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestingWebApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

}
