package com.chr.picy.config;

import com.chr.picy.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 15:20
 * @Description:
 */
@Configuration
public class webConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/")
//                .excludePathPatterns("/configure")
//                .excludePathPatterns("/js/**", "/css/**", "/img/**", "/favicon.ico");
    }
}
