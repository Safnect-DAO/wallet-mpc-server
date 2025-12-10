package com.safnect.wallet.mpc.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Objects;

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
        String path = "D:\\yml\\safnect_wallet.yml";
        
        File configFile = new File(path);
        if (!configFile.exists()) {
            String path1 = "/home/yml/safnect_wallet.yml";
            File configFile1 = new File(path1);
            if (configFile1.exists()) {
                path = path1;
            } else {
                path = Objects.requireNonNull(getConfigPath(), "配置路径为空");
            }
        }
        
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        try {
            yamlProperties.setResources(new FileUrlResource(path));
            propertySourcesPlaceholderConfigurer.setProperties(
                Objects.requireNonNull(yamlProperties.getObject(), "未能解析到配置文件内容")
            );
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
        
        // 3. 默认路径（可同时兼容本地启动与部署目录）
        Path baseDir = resolveAppDir();
        Path[] defaultPaths = new Path[] {
            baseDir.resolve("home/yml/safnect_wallet.yml"),           // jar 所在目录下
            Paths.get(System.getProperty("user.dir"), "home/yml/safnect_wallet.yml") // 当前工作目录
        };
        
        for (Path defaultPath : defaultPaths) {
            if (Files.exists(defaultPath)) {
                return defaultPath.toString();
            }
        }
        
        // 如果都找不到，仍然返回默认路径（让调用者处理异常）
        return baseDir.resolve("home/yml/safnect_wallet.yml").toString();
    }

    /**
     * 尝试获取应用所在目录（优先使用 jar 位置，退回到工作目录）
     */
    private Path resolveAppDir() {
        try {
            CodeSource codeSource = YamlPropertyConfig.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                Path path = Paths.get(codeSource.getLocation().toURI()).toAbsolutePath();
                // 如果是文件（通常为 jar），取其父目录；否则直接取目录
                if (Files.isRegularFile(path)) {
                    return path.getParent();
                }
                if (Files.isDirectory(path)) {
                    return path;
                }
            }
        } catch (URISyntaxException ignored) {
            // ignore and fallback
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

}