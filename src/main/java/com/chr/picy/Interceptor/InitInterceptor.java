package com.chr.picy.Interceptor;

import com.aliyun.oss.OSS;
import com.chr.picy.Bean.OssClientManager;
import com.chr.picy.util.OSSUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: 程浩然
 * @Create: 2025/3/3 - 10:22
 * @Description:
 */
@Slf4j
@Component
public class InitInterceptor implements HandlerInterceptor {

    @Value("${picy.notice}")
    String notice;
    @Autowired
    private OssClientManager ossClientManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        log.info("设置公告内容...");
        session.setAttribute("notice", notice);

        // 注入OSS连接
        String accessKeyId = (String) session.getAttribute("accessKeyId");
        String accessKeySecret = (String) session.getAttribute("accessKeySecret");
        String bucketName = (String) session.getAttribute("bucketName");

        if (accessKeyId != null && accessKeySecret != null && bucketName != null) {
            log.info("从 session 读取 OSS 登录信息");
            OSS ossClient = OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);
            // 将 OSS 放在 Spring 里管理
            ossClientManager.setOssClient(ossClient, bucketName);
            log.info("成功注入OSS连接...");
        }
        return true;
    }
}
