package com.chr.picy.controller;

import com.aliyun.oss.OSS;
import com.chr.picy.Bean.SecretKey;
import com.chr.picy.service.OssClientManager;
import com.chr.picy.util.OSSUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private OssClientManager ossClientManager;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<String> notices = Arrays.asList(
                "PicX v3.0 进一步简化用户操作，统一使用内置的仓库和分支",
                "如果你需要继续使用自定义的仓库和分支，请使用 PicX v2.0"
        );
        model.addAttribute("notices", notices);

        // 检查 session
        String accessKeyId = (String) session.getAttribute("accessKeyId");
        String accessKeySecret = (String) session.getAttribute("accessKeySecret");
        String bucketName = (String) session.getAttribute("bucketName");

        if (accessKeyId != null && accessKeySecret != null && bucketName != null) {
            log.info("从 session 读取 OSS 登录信息");
            OSS ossClient = OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);
            // 将 OSS 放在 Spring 里管理
            ossClientManager.setOssClient(ossClient, bucketName);
            return "index";
        }
        return "index";
    }


    // 登录
    @PostMapping("/configure")
    public ResponseEntity<?> configureOss(@RequestBody SecretKey secretKey, HttpSession session) {
        String accessKeyId = secretKey.getAccessKeyId();
        String accessKeySecret = secretKey.getAccessKeySecret();
        String bucketName = secretKey.getBucketName();
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

            // 存储到 OSS 的 setting.json
            Map<String, String> settings = new HashMap<>();
            settings.put("accessKeyId", accessKeyId);
            settings.put("accessKeySecret", accessKeySecret);
            settings.put("bucketName", bucketName);
            OSSUtil.saveSettingsToOSS(ossClient, bucketName, settings);

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
        Map map = OSSUtil.readJsonFromOSS(ossClient, ossClientManager.getBucketName(), "setting.json");
        log.info("读取的内容{}", map);
        return "index";  // 或者根据需求返回配置信息
    }

    // 发送设置的状态到后端,写入json文件
    @PostMapping("/save-settings")
    public ResponseEntity<?> saveSettings(@RequestBody Map<String, String> settings) {
        log.info("进行设置参数{}", settings);
        OSS ossClient = ossClientManager.getOssClient();
        try {
            OSSUtil.saveSettingsToOSS(ossClient, ossClientManager.getBucketName(), settings);
        } catch (Exception e) {
            log.error("json配置文件上传到OSS失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "json配置文件上传到OSS失败"));
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "Settings saved successfully"));
    }

}

/*
 * 图片水印
 * 压缩图片
 * 压缩算法
 * 转换Markdown
 *
 * 目录
 *
 * 秘钥key
 * 秘钥val
 * 存储桶
 */