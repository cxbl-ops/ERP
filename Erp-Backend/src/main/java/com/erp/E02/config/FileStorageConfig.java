package com.erp.E02.config;

import com.erp.E02.service.FileStorageService;
import com.erp.E02.service.impl.LocalFileStorageService;
import com.erp.E02.service.impl.MinioFileStorageService;
import com.erp.E02.service.impl.OSSFileStorageService;
import com.erp.E02.service.impl.S3FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.mode}")
    private String storageMode;

    @Autowired
    private LocalFileStorageService localFileStorageService;

    @Autowired
    private MinioFileStorageService minioFileStorageService;

    @Autowired
    private OSSFileStorageService ossFileStorageService;

    @Autowired
    private S3FileStorageService s3FileStorageService;

    @Bean
    @Primary
    public FileStorageService fileStorageService() {
        return switch (storageMode.toLowerCase()) {
            case "local" -> localFileStorageService;
            case "minio" -> minioFileStorageService;
            case "oss" -> ossFileStorageService;
            case "s3" -> s3FileStorageService;
            default -> throw new IllegalArgumentException("Unsupported storage mode: " + storageMode);
        };
    }
}

