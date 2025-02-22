package com.chr.picy.controller;

import com.aliyun.oss.OSS;
import com.chr.picy.util.JWTUtil;
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
public class picyConfig {
    @Autowired
    private OSS ossClient;

    @GetMapping("/")
    public String home(Model model) {
        List<String> notices = Arrays.asList(
                "PicX v3.0 进一步简化用户操作，统一使用内置的仓库和分支",
                "如果你需要继续使用自定义的仓库和分支，请使用 PicX v2.0"
        );
        model.addAttribute("notices", notices);
        return "index";
    }

    // 登录并保存配置到 Session
    @PostMapping("/configure")
    public ResponseEntity<?> configureOss(@RequestBody Map<String, String> config, HttpSession session) {
        String accessKeyId = config.get("accessKeyId");
        String accessKeySecret = config.get("accessKeySecret");
        String bucketName = config.get("bucketName");

        try {
            // 保存到 Session 中
            session.setAttribute("accessKeyId", accessKeyId);
            session.setAttribute("accessKeySecret", accessKeySecret);
            session.setAttribute("bucketName", bucketName);

            // 生成 JWT
            String token = JWTUtil.getJWTToken(accessKeyId, accessKeySecret, bucketName);

            // 获取位置
            String location = ossClient.getBucketLocation(bucketName);
            System.out.println(location);

            // 返回成功信息
            Map<String, String> response = new HashMap<>();
            response.put("location", location);
            response.put("repo", bucketName);

            // 将 JWT 存入 Cookie
            return ResponseEntity.ok()
                    .header("Set-Cookie", "token=" + token + "; HttpOnly; Path=/; Max-Age=1296000")
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "配置失败：" + e.getMessage()));
        }
    }

    // 获取配置信息
    @GetMapping("/get-settings")
    public String getSettings(HttpSession session) {
        String accessKeyId = (String) session.getAttribute("accessKeyId");
        String accessKeySecret = (String) session.getAttribute("accessKeySecret");
        String bucketName = (String) session.getAttribute("bucketName");

        // 返回用户当前配置
        Map<String, String> settings = new HashMap<>();
        settings.put("accessKeyId", accessKeyId);
        settings.put("accessKeySecret", accessKeySecret);
        settings.put("bucketName", bucketName);
        log.info("连接{}", ossClient);
        // 在前端展示
        return "index";  // 或者根据需求返回配置信息
    }

    // 发送设置的状态到后端,写入json文件
    @PostMapping("/save-settings")
    public String saveSettings(@RequestBody Map<String, Object> settings) {
        log.info("进行设置参数");
        return "index";
    }

}
