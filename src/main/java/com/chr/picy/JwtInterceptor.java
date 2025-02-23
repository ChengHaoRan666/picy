package com.chr.picy;

import com.chr.picy.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 16:37
 * @Description:
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.sendRedirect("/");
            return false;
        }

        Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst();

        if (tokenCookie.isEmpty() || JWTUtil.isTokenExpired(tokenCookie.get().getValue())) {
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
