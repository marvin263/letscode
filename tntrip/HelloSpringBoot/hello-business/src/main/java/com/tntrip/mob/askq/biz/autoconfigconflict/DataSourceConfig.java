package com.tntrip.mob.askq.biz.autoconfigconflict;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Created by libing2 on 2016/11/10.
 */
@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource4Order() {
        DruidDataSource dds = new DruidDataSource();
        dds.setUrl("orderUrl");
        return dds;
    }

    @Bean
    public DataSource dataSource4Customer() {
        DruidDataSource dds = new DruidDataSource();
        dds.setUrl("customerUrl");
        return dds;
    }
}
