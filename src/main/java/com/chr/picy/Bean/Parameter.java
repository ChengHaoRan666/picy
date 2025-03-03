package com.chr.picy.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 程浩然
 * @Create: 2025/3/3 - 10:07
 * @Description: 配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    /**
     * 是否压缩图片
     */
    Boolean compress;
    /**
     * 压缩图片算法（当compress为turn时才生效）
     */
    String compressionAlgorithm;
    /**
     * 是否转换为Markdown格式（为false时上传后默认复制图片链接，为true时上传后获取Markdown格式链接）
     */
    Boolean ConvertMarkdown;
    /**
     * 名称是否哈希化
     */
    Boolean Hashization = true;
    /**
     * 上传目录
     */
    String catalogue;
    /**
     * 秘钥key
     */
    String accessKeyId;
    /**
     * 秘钥val
     */
    String accessKeySecret;
    /**
     * 存储桶
     */
    String bucketName;
}
