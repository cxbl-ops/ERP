package com.erp.E02.controller;

import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;

@Tag(name = "文件上传", description = "文件上传 、文件下载管理")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    @Resource
    private final FileStorageService fileStorageService;

    @StandardCreateApi(summary = "文件上传", description = "文件上传")
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("收到图片啦~");
        try {
            String file_url = fileStorageService.uploadFile(file);
            System.out.println("返回图片url路径: " + file_url);
            return file_url;
        } catch (IOException e) {
            return "文件上传失败: " + e.getMessage();
        }
    }

    @StandardCreateApi(summary = "文件下载", description = "文件下载")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileStorageService.downloadFile(fileName);
            // 使用 Apache Tika 来识别文件的 MIME 类型
            Tika tika = new Tika();
            String fileType = tika.detect(fileContent); // 获取文件的 MIME 类型
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(fileType)) // 设置正确的文件类型
                    .body(fileContent);
        } catch (IOException e) {
            return null;
        }
    }
}

