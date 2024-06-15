package com.safnect.wallet.mpc.config;

import java.io.File;
import java.net.MalformedURLException;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileUrlResource;

@Configuration
public class YamlPropertyConfig {

    /**
     * 将yaml文件转为properties并设置到属性源
     * @return
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        String path = "D:\\yml\\safnect_wallet.yml";
        if (!new File(path).exists()) {
        	path = "/home/yml/safnect_wallet.yml";
        }
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        try {
			yamlProperties.setResources(new FileUrlResource(path));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        propertySourcesPlaceholderConfigurer.setProperties(yamlProperties.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

}