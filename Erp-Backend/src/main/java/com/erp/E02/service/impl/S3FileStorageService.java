package com.erp.E02.service.impl;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import com.erp.E02.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.model.*;
import java.net.URI;

@Service
public class S3FileStorageService implements FileStorageService {

    @Value("${file.storage.s3.endpoint}")
    private String endpoint;

    @Value("${file.storage.s3.accessKey}")
    private String accessKey;

    @Value("${file.storage.s3.secretKey}")
    private String secretKey;

    @Value("${file.storage.s3.bucketName}")
    private String bucketName;

    private S3Client s3Client;


    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("us-east-1")) // 根据实际情况选择区域
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getOriginalFilename())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            // 构造文件访问URL
            String fileUrl = "http://" + bucketName + ".s3.amazonaws.com/" + file.getOriginalFilename();
            System.out.println("上传成功，文件访问URL：" + fileUrl);
            return fileUrl;
        }
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            s3Object.transferTo(outputStream);
            return outputStream.toByteArray();
        }
    }
}

