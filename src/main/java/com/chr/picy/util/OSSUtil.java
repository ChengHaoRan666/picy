package com.chr.picy.util;


import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.GenericRequest;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 11:43
 * @Description: OSS的辅助方法
 */

public class OSSUtil {

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
}
