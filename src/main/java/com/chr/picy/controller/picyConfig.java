package com.chr.picy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class picyConfig {
    @GetMapping("/")
    public String home(Model model) {
        List<String> notices = Arrays.asList(
                "PicX v3.0 进一步简化用户操作，统一使用内置的仓库和分支",
                "如果你需要继续使用自定义的仓库和分支，请使用 PicX v2.0"
        );
        model.addAttribute("notices", notices);
        return "index";
    }

    // 发送设置的状态到后端
    @PostMapping("/api/save-settings")
    public String saveSettings(@RequestBody Map<String, Boolean> settings) {
        log.info("后端接收到发送的信息: {}", settings);
        return "index";
    }

    // 从后端获取配置信息返回给前端
    @GetMapping("/api/get-settings")
    public String getSettings() {
        return "index";
    }

    // 获取用户头像
    @RequestMapping("/api/user/info")
    public String getUserInfo() {
        return "index";
    }
}
