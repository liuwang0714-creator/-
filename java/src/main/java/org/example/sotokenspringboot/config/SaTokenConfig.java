package org.example.sotokenspringboot.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    // 注册 Sa-Token 拦截器，开启注解功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter
                    .match("/**")
                    .notMatch("/user/login", "/user/register","/images/**","/user/wxlogin")
                    .check(r -> StpUtil.checkLogin());

            SaRouter.match("/admin/**", r -> StpUtil.checkRole("admin"));

            SaRouter.match("/goods/**")
                    .notMatch("/goods/goodslist")
                    .notMatch("/goods/selectid")
                    .notMatch("/goods/search")
                    .check(r -> StpUtil.checkRoleOr("merchant", "admin"));
            SaRouter.match("/order/user/**")
                    .check(r -> StpUtil.checkRoleOr("user", "admin"));
            SaRouter.match("/order/merchant/**")
                    .check(r -> StpUtil.checkRoleOr("merchant", "admin"));
            SaRouter.match("/goods/list", r -> StpUtil.checkLogin());

        })).addPathPatterns("/**");
    }
}