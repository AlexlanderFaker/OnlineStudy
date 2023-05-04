package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String point;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(point)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
