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
     * 配置文件路径优先级：
     * 1. 环境变量 CONFIG_PATH
     * 2. 系统属性 config.path
     * 3. 默认路径（Windows: D:\yml\safnect_wallet.yml, Linux/Mac: ./home/yml/safnect_wallet.yml）
     * @return
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        String path = getConfigPath();
        
        File configFile = new File(path);
        if (!configFile.exists()) {
            throw new RuntimeException("配置文件不存在: " + path + 
                "\n请设置环境变量 CONFIG_PATH 或系统属性 -Dconfig.path 指定配置文件路径");
        }
        
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        try {
            yamlProperties.setResources(new FileUrlResource(path));
            propertySourcesPlaceholderConfigurer.setProperties(yamlProperties.getObject());
            System.out.println("成功加载配置文件: " + path);
        } catch (MalformedURLException e) {
            throw new RuntimeException("配置文件路径格式错误: " + path, e);
        }
        return propertySourcesPlaceholderConfigurer;
    }
    
    /**
     * 获取配置文件路径
     * 优先级：环境变量 > 系统属性 > 默认路径
     */
    private String getConfigPath() {
        // 1. 优先使用环境变量
        String envPath = System.getenv("CONFIG_PATH");
        if (envPath != null && !envPath.isEmpty()) {
            File file = new File(envPath);
            if (file.exists()) {
                return envPath;
            }
        }
        
        // 2. 使用系统属性
        String sysPath = System.getProperty("config.path");
        if (sysPath != null && !sysPath.isEmpty()) {
            File file = new File(sysPath);
            if (file.exists()) {
                return sysPath;
            }
        }
        
        // 3. 默认路径（兼容旧代码）
        String[] defaultPaths = {
            "D:\\yml\\safnect_wallet.yml",  // Windows 默认路径
            "/Users/ruijie/Desktop/WB/qianbao/wallet-mpc-server/home/yml/safnect_wallet.yml",  // Mac 开发路径
            "./home/yml/safnect_wallet.yml",  // 相对路径（推荐用于部署）
            "../home/yml/safnect_wallet.yml"  // 相对路径（备用）
        };
        
        for (String defaultPath : defaultPaths) {
            File file = new File(defaultPath);
            if (file.exists()) {
                return defaultPath;
            }
        }
        
        // 如果都找不到，返回相对路径（让调用者处理异常）
        return "./home/yml/safnect_wallet.yml";
    }

}