package org.example.sotokenspringboot;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SoTokenspringBootApplication {
    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(SoTokenspringBootApplication.class, args);
    }
}