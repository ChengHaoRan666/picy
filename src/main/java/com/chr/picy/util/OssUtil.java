package com.chr.picy.util;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.BucketInfo;
import org.springframework.stereotype.Component;

/**
 * @Author: 程浩然
 * @Create: 2025/2/21 - 11:43
 * @Description:
 */
@Component
public class OssUtil {

    public  String getRegionFromBucket(String accessKeyId, String accessKeySecret, String bucketName) {
        // 先用一个默认的公共 endpoint 连接
        String defaultEndpoint = "https://oss-cn-hangzhou.aliyuncs.com";

        OSS ossClient = new OSSClientBuilder().build(defaultEndpoint, accessKeyId, accessKeySecret);
        try {
            BucketInfo bucketInfo = ossClient.getBucketInfo(new GenericRequest(bucketName));
            return bucketInfo.getBucket().getLocation(); // 获取 region
        } finally {
            ossClient.shutdown();
        }
    }
}
