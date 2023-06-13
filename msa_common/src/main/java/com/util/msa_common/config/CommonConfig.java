package com.per.msa_common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class CommonConfig {
    @Value("${config_properties}")
    String configPropFile;

    @Bean(name = "config")
    public PropertiesFactoryBean config() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource(configPropFile));
        return bean;
    }
}
