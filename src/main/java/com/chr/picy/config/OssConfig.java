package com.chr.picy.config;

import com.aliyun.oss.OSS;
import com.chr.picy.util.JWTUtil;
import com.chr.picy.util.OSSUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * 配置 OSS 客户端，确保 OSS 客户端是 Spring 管理的 Bean
 */
@Component
public class OssConfig {
    @Autowired
    private HttpServletRequest request;

    public OSS getOssClient() {
        // 从 Cookie 中获取 JWT
        String token = getCookieValue("token");

        if (token == null||JWTUtil.isTokenExpired(token)) {
            throw new IllegalStateException("请先登录！");
        }

        // 从 JWT 中解析出配置信息
        try {
            String accessKeyId = (String) JWTUtil.parseToken(token).get("accessKeyId");
            String accessKeySecret = (String) JWTUtil.parseToken(token).get("accessKeySecret");
            String bucketName = (String) JWTUtil.parseToken(token).get("bucketName");

            if (accessKeyId == null || accessKeySecret == null || bucketName == null) {
                throw new IllegalStateException("请先配置 OSS 信息！");
            }

            return OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);
        } catch (Exception e) {
            throw new IllegalStateException("解析 JWT 配置失败，可能已过期或无效！", e);
        }
    }

    @Bean
    public OSS ossClient() {
        return getOssClient();
    }

    // 从 Cookie 中获取指定名称的 Cookie 值
    private String getCookieValue(String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookie = Arrays.stream(cookies)
                    .filter(c -> cookieName.equals(c.getName()))
                    .findFirst();
            return cookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }
}
