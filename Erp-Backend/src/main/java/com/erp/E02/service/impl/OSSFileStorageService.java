package com.erp.E02.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.erp.E02.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class OSSFileStorageService implements FileStorageService {

    @Value("${file.storage.oss.endpoint}")
    private String endpoint;

    @Value("${file.storage.oss.accessKey}")
    private String accessKey;

    @Value("${file.storage.oss.secretKey}")
    private String secretKey;

    @Value("${file.storage.oss.bucketName}")
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), inputStream));
            // 构造文件访问URL
            String fileUrl = "http://" + bucketName + "." + endpoint + "/" + file.getOriginalFilename();
            System.out.println("上传成功，文件访问URL：" + fileUrl);
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ossClient.getObject(new GetObjectRequest(bucketName, fileName));
            return outputStream.toByteArray();
        }
    }
}

