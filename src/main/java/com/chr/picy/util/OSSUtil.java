package com.chr.picy.util;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 11:43
 * @Description: OSS的辅助方法
 */
@Slf4j
public class OSSUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 向setting.json文件里存配置信息
     * @param ossClient
     * @param bucketName
     * @param settings
     * @throws Exception
     */
    public static void saveSettingsToOSS(OSS ossClient, String bucketName, Map<String, String> settings) throws Exception {
        String jsonString = objectMapper.writeValueAsString(settings);
        InputStream inputStream = new java.io.ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        ossClient.putObject(new PutObjectRequest(bucketName, "setting.json", inputStream));
    }


    /**
     * 通过 accessKeyId 和 accessKeySecret 和 bucketName 获取 region
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param bucketName
     * @return region
     */
    public static String getRegionFromBucket(String accessKeyId, String accessKeySecret, String bucketName) {
        // 先用一个默认的公共 endpoint 连接
        String defaultEndpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(defaultEndpoint, accessKeyId, accessKeySecret);
            BucketInfo bucketInfo = ossClient.getBucketInfo(new GenericRequest(bucketName));
            String region = bucketInfo.getBucket().getLocation();
            // 如果 region 以 "oss-" 开头，去掉 "oss-"
            if (region.startsWith("oss-")) {
                region = region.substring(4);
            }
            return region; // 获取 region
        } finally {
            if (ossClient != null) ossClient.shutdown(); // 关闭 OSS 客户端，避免资源泄露
        }
    }


    /**
     * 获取OSS连接对象
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param bucketName
     * @return
     */
    public static OSS getOssClient(String accessKeyId, String accessKeySecret, String bucketName) {
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


    /**
     * 获取 json 文件内容
     * @param ossClient
     * @param bucketName
     * @param jsonFile
     * @return
     */
    public static Map readJsonFromOSS(OSS ossClient, String bucketName, String jsonFile) {
        try {
            // 获取 OSS 上的 JSON 文件对象
            OSSObject ossObject = ossClient.getObject(bucketName, jsonFile);
            InputStream content = ossObject.getObjectContent();

            // 解析 JSON 并返回 Map
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            log.error("读取 OSS JSON 文件失败: {}", jsonFile, e);
            return null;
        }
    }
}
