package com.chr.picy;

import com.chr.picy.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 16:37
 * @Description:
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        // 如果token是空或者空值或者token过期，删除token，跳转到登录
        if (token == null || token.isEmpty() || jwtUtil.isTokenExpired(token)) {
            Cookie deleteCookie = new Cookie("token", "");
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            response.addCookie(deleteCookie);
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
