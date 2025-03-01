package com.chr.picy.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 程浩然
 * @Create: 2025/2/24 - 8:26
 * @Description: 秘钥
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretKey {

    String accessKeyId;

    String accessKeySecret;

    String bucketName;
}
