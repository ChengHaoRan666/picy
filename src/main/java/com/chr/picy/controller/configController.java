package com.chr.picy.controller;

import com.aliyun.oss.OSS;
import com.chr.picy.Bean.SecretKey;
import com.chr.picy.util.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Slf4j
@Controller
public class configController {

    @GetMapping("/")
    public String home(Model model) {
        List<String> notices = Arrays.asList(
                "PicX v3.0 进一步简化用户操作，统一使用内置的仓库和分支",
                "如果你需要继续使用自定义的仓库和分支，请使用 PicX v2.0"
        );
        model.addAttribute("notices", notices);
        return "index";
    }

    // 登录
    @PostMapping("/configure")
    public ResponseEntity<?> configureOss(@RequestBody SecretKey secretKey) {
        String accessKeyId = secretKey.getAccessKeyId();
        String accessKeySecret = secretKey.getAccessKeySecret();
        String bucketName = secretKey.getBucketName();
        log.info("传入的信息：{}, {}, {}", accessKeyId, accessKeySecret, bucketName);

        // 创建 OSS 客户端
        OSS ossClient;
        try {
            // 获取 OSS 连接
            ossClient = OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);

            // 获取存储桶地点
            String location = ossClient.getBucketLocation(bucketName);

            // 返回成功信息
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("location", location);
            responseMap.put("repo", bucketName);

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "配置失败"));
        }
    }


    // 获取配置信息
    @GetMapping("/get-settings")
    public String getSettings() {
        log.info("获取配置信息");
        return "index";  // 或者根据需求返回配置信息
    }

    // 发送设置的状态到后端,写入json文件
    @PostMapping("/save-settings")
    public String saveSettings(@RequestBody Map<String, Object> settings) {
        log.info("进行设置参数{}", settings);
        return "index";
    }

}
