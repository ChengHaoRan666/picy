package com.chr.picy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 13:16
 * @Description: JWT登录相关工具类
 */
@Component
public class JWTUtil {
    @Value("${jwt.secret-key}")
    private static String secretKey;

    @Value("${jwt.ttl}")
    private static long ttl;


    /**
     * 生成JWT令牌
     *
     * @return
     */
    public static String getJWTToken(String accessKeyId, String accessKeySecret, String bucketName) {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("accessKeyId", accessKeyId);
        credentials.put("accessKeySecret", accessKeySecret);
        credentials.put("bucketName", bucketName);
        return Jwts.builder()
                .setClaims(credentials) // 存储 accessKeyId, accessKeySecret, bucketName
                .setExpiration(new Date(System.currentTimeMillis() + ttl)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, secretKey) // 签名算法
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查JWT是否过期
     */
    public static boolean isTokenExpired(String token) {
        if (token == null || token.isEmpty())
            return true;
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取新的JWT并延长过期时间
     */
    public static String refreshToken(String oldToken) {
        Claims claims = parseToken(oldToken);  // 解析旧的Token
        // 根据旧的Token信息生成新的Token（重新设置有效期）
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + ttl))  // 设置新的过期时间
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
