package com.chr.picy.util;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.GenericRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 11:43
 * @Description: OSS的辅助方法
 */
@Component
public class OSSUtil {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JWTUtil jwtUtil;

    public static String getRegionFromBucket(String accessKeyId, String accessKeySecret, String bucketName) {
        // 先用一个默认的公共 endpoint 连接
        String defaultEndpoint = "https://oss-cn-hangzhou.aliyuncs.com";

        OSS ossClient = new OSSClientBuilder().build(defaultEndpoint, accessKeyId, accessKeySecret);
        try {
            BucketInfo bucketInfo = ossClient.getBucketInfo(new GenericRequest(bucketName));
            String region = bucketInfo.getBucket().getLocation();
            // 如果 region 以 "oss-" 开头，去掉 "oss-"
            if (region.startsWith("oss-")) {
                region = region.substring(4);
            }
            return region; // 获取 region
        } finally {
            ossClient.shutdown();
        }
    }


    public static OSS getOssClient() {
        Claims claims = jwtutil.parseToken(getCookieValue("token"));
        String accessKeyId = (String) claims.get("accessKeyId");
        String accessKeySecret = (String) claims.get("accessKeySecret");
        String bucketName = (String) claims.get("bucketName");

        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        String region = getRegionFromBucket(accessKeyId, accessKeySecret, bucketName);
        String endpoint = "https://oss-" + region + ".aliyuncs.com"; // 自动拼接 endpoint
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    // 从 Cookie 中获取指定名称的 Cookie 值
    private static String getCookieValue(String cookieName) {
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
