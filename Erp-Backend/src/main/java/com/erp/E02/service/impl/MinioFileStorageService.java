package com.erp.E02.service.impl;

import com.erp.E02.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioFileStorageService implements FileStorageService {

    @Value("${file.storage.minio.endpoint}")
    private String endpoint;

    @Value("${file.storage.minio.accessKey}")
    private String accessKey;

    @Value("${file.storage.minio.secretKey}")
    private String secretKey;

    @Value("${file.storage.minio.bucketName}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(inputStream, file.getSize(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );

            // 生成文件的公开 URL
            String fileUrl = endpoint + "/" + bucketName + "/" + file.getOriginalFilename();
            System.out.println("上传成功，文件访问URL：" + fileUrl);
            return fileUrl;
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("MinIO 文件上传失败", e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return outputStream.toByteArray();
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("MinIO 文件下载失败", e);
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
