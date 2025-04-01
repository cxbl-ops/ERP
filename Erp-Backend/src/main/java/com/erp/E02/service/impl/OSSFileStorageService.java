package com.erp.E02.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.erp.E02.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class OSSFileStorageService implements FileStorageService {

    @Value("${file.storage.oss.endpoint}")
    private String endpoint;

    @Value("${file.storage.oss.accessKey}")
    private String accessKeyId;

    @Value("${file.storage.oss.secretKey}")
    private String secretKey;

    @Value("${file.storage.oss.bucketName}")
    private String bucketName;

    @Value("${file.storage.oss.region}")
    private String region;

    private OSS ossClient;

    private boolean bucketExists;

    @PostConstruct
    public void init() {
        // 初始化OSS客户端
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, secretKey);

        try {
            bucketExists = ossClient.doesBucketExist(bucketName);
            log.info("Bucket: "+bucketName + " exists状态: " + bucketExists);
        } catch (OSSException e) {
            log.error("oss初始化错误："+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS客户端已关闭");
        }
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            //构造文件随机名，避免重复

            //获取原始文件名后缀 添加判空操作
            String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            //根据格式化时间生成文件路径
            String path = new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"/";
            //生成随机文件名
            String fileName = path+ UUID.randomUUID() + extension;

            ossClient.putObject(new PutObjectRequest(bucketName, fileName, inputStream));
            // 构造文件访问URL
            String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;
            log.info("上传成功，文件访问URL：" + fileUrl);;
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

    @Override
    public String uploadFileChunk(MultipartFile file, int chunk, int totalChunks, String fileName) throws IOException {
        return "";
    }

    @Override
    public byte[] downloadFileRange(String fileName, String range) throws IOException {
        return new byte[0];
    }
}

