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
    private  String secretKey;

    @Value("${jwt.ttl}")
    private  long ttl;

    @Value("${jwt.token-name}")
    private  String tokenName;

    /**
     * 生成JWT令牌
     *
     * @return
     */
    public  String getJWTToken(String accessKeyId, String accessKeySecret, String bucketName) {
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
    public  Claims parseToken() {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(tokenName)
                .getBody();
    }

    /**
     * 检查JWT是否过期
     */
    public  boolean isTokenExpired() {
        try {
            Claims claims = parseToken();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
