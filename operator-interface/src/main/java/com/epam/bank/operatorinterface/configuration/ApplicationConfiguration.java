package com.epam.bank.operatorinterface.configuration;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Core configuration class for general special beans of configuration stuff.
 * Create specific configuration classes for specific configurations, for instance for security purposes.
 */
@Configuration
public class ApplicationConfiguration {

    //TODO fix property source
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
