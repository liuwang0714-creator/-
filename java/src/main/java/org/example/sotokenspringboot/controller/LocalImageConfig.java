package org.example.sotokenspringboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Configuration
public class LocalImageConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     * 访问格式：http://localhost:8081/images/图片名.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 前端访问路径：/images/**  对应本地磁盘路径
        // 2. 本地路径写法：Windows用 D:/images/  ；Linux/Mac用 /home/user/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:E:\\pg_file\\images");
    }
}
