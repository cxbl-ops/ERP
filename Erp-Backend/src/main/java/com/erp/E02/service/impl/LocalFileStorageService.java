package com.erp.E02.service.impl;

import com.erp.E02.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.local.path}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        Path path = Paths.get(uploadDir, file.getOriginalFilename());
        Files.createDirectories(path.getParent()); // 创建目录
        file.transferTo(path.toFile());

        // 构造文件访问URL
        String fileUrl = "http://localhost:8080/files/download/" + file.getOriginalFilename();
        System.out.println("上传成功，文件访问URL：" + fileUrl);
        return fileUrl;
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        return Files.readAllBytes(path);
    }
}

