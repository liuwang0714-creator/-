package org.example.sotokenspringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")          // 允许所有接口
                .allowedOriginPatterns("*") // 允许所有来源（前端地址）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许请求方式
                .allowCredentials(true)      // 允许携带Cookie
                .maxAge(3600);               // 预检有效期
    }
}