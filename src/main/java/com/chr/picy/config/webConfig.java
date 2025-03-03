package com.chr.picy.config;

import com.chr.picy.Interceptor.InitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: 程浩然
 * @Create: 2025/3/3 - 10:24
 * @Description:
 */
@Configuration
public class webConfig implements WebMvcConfigurer {
    @Autowired
    private InitInterceptor initInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initInterceptor)
                .addPathPatterns("/")
                .excludePathPatterns();
    }
}
