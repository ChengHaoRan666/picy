package com.chr.picy.controller;

import com.aliyun.oss.OSS;
import com.chr.picy.util.JWTUtil;
import com.chr.picy.util.OSSUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Controller
@Slf4j
public class picyConfig {
    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/")
    public String home(Model model, @CookieValue(value = "token", required = false) String token, HttpServletResponse response) {
        List<String> notices = Arrays.asList(
                "PicX v3.0 进一步简化用户操作，统一使用内置的仓库和分支",
                "如果你需要继续使用自定义的仓库和分支，请使用 PicX v2.0"
        );
        model.addAttribute("notices", notices);
        return "index";
    }

    // 发送设置的状态到后端,写入json文件
    @PostMapping("/save-settings")
    public String saveSettings(@RequestBody Map<String, Object> settings) {
        log.info("进行设置参数");
        return "index";
    }

    // 从后端获取配置信息返回给前端，从json文件中读
    @GetMapping("/get-settings")
    public String getSettings() {
        log.info("读取参数配置");
        return "index";
    }


    // 登录
    @PostMapping("/configure")
    public ResponseEntity<?> configureOss(@RequestBody Map<String, String> config) {
        String accessKeyId = config.get("accessKeyId");
        String accessKeySecret = config.get("accessKeySecret");
        String bucketName = config.get("bucketName");

        try {
            // 获取OSS连接
            OSS ossClient = OSSUtil.getOssClient(accessKeyId, accessKeySecret, bucketName);
            // 生成 JWT
            String token = jwtUtil.getJWTToken(accessKeyId, accessKeySecret, bucketName);

            // 获取位置
            String location = ossClient.getBucketLocation(bucketName);
            System.out.println(location);

            // 返回成功信息
            Map<String, String> response = new HashMap<>();
            response.put("location", location);
            response.put("repo", bucketName);
            // 将 JWT 存入 Cookie
            return ResponseEntity.ok()
                    .header("Set-Cookie", "token=" + token + "; HttpOnly; Path=/")
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "配置失败：" + e.getMessage()));
        }
    }
}