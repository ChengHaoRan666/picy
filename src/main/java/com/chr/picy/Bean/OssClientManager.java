package com.chr.picy.Bean;


import com.aliyun.oss.OSS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author: 程浩然
 * @Create: 2025/3/1 - 16:54
 * @Description:
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OssClientManager {

    private OSS ossClient;
    private String bucketName;

    public void setOssClient(OSS ossClient, String bucketName) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
    }

    public boolean isConfigured() {
        return ossClient != null;
    }

    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
    }
}
