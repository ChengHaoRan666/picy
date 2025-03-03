package com.chr.picy.controller;

import com.aliyun.oss.OSS;
import com.chr.picy.Bean.OssClientManager;
import com.chr.picy.Bean.Parameter;
import com.chr.picy.util.OSSUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Controller
public class configController {
    @Autowired
    private OssClientManager ossClientManager;

    @GetMapping("/")
    public String index() {
        return "index";
    }


    // 登录
    @PostMapping("/configure")
    public ResponseEntity<?> configureOss(@RequestBody Parameter parameter, HttpSession session) {
        String accessKeyId = parameter.getAccessKeyId();
        String accessKeySecret = parameter.getAccessKeySecret();
        String bucketName = parameter.getBucketName();
        log.info("传入的信息：{}, {}, {}", accessKeyId, accessKeySecret, bucketName);
        try {
            // 获取 OSS 连接
            OSS ossClient = OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);
            ossClientManager.setOssClient(ossClient, bucketName);
            log.info("OSS 客户端已成功存入 Spring 容器");

            // 获取存储桶信息
            String location = ossClient.getBucketLocation(bucketName);

            //  存储到 Session
            session.setAttribute("accessKeyId", accessKeyId);
            session.setAttribute("accessKeySecret", accessKeySecret);
            session.setAttribute("bucketName", bucketName);

            // 将配置同步到云端
            OSSUtil.saveSettingsToOSS(ossClient, bucketName, parameter);

            return ResponseEntity.ok(Map.of("location", location, "repo", bucketName));
        } catch (Exception e) {
            log.error("OSS 连接失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "配置失败"));
        }
    }


    // 获取配置信息
    @GetMapping("/get-settings")
    public String getSettings() {
        OSS ossClient = ossClientManager.getOssClient();
        Parameter parameter = OSSUtil.readJsonFromOSS(ossClient, ossClientManager.getBucketName(), "setting.json");
        log.info("读取的内容{}", parameter);
        return "index";  // 或者根据需求返回配置信息
    }

    // 发送设置的状态到后端,写入json文件
    @PostMapping("/save-settings")
    public ResponseEntity<?> saveSettings(@RequestBody Parameter parameter) {
        log.info("进行设置参数{}", parameter);
        OSS ossClient = ossClientManager.getOssClient();
        try {
            OSSUtil.saveSettingsToOSS(ossClient, ossClientManager.getBucketName(), parameter);
        } catch (Exception e) {
            log.error("json配置文件上传到OSS失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "json配置文件上传到OSS失败"));
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "Settings saved successfully"));
    }

}

/*
 * 压缩图片
 * 压缩算法
 * 转换Markdown
 * 名称哈希化
 *
 * 目录
 *
 * 秘钥key
 * 秘钥val
 * 存储桶
 */