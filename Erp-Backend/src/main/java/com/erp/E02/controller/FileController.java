package com.erp.E02.controller;

import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.service.FileStorageService;
import com.erp.E02.utils.FileUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;
import java.io.IOException;

@Tag(name = "文件上传下载", description = "文件上传 、文件下载管理")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.storage.local.path}")
    private String uploadDir;

    @Resource
    private final FileStorageService fileStorageService;

    /**
     * 普通上传
     * @param file
     * @return
     */
    @StandardCreateApi(summary = "文件普通上传", description = "文件普通上传")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("上传文件: " + file.getOriginalFilename());
        try {
            String file_url = fileStorageService.uploadFile(file);
            System.out.println("返回文件url: " + file_url);
            return ResponseEntity.ok(file_url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败");
        }
    }

    /**
     * 分片上传
     * @param file
     * @param chunk
     * @param totalChunks
     * @param fileName
     * @return
     */
    @StandardCreateApi(summary = "文件分片上传", description = "文件分片上传")
    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunk") int chunk,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName
    ) {
        try {
            String fileUrl = fileStorageService.uploadFileChunk(file, chunk, totalChunks, fileName);
            if (fileUrl != null) System.out.println("返回文件url: " + fileUrl);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败");
        }
    }

    /**
     * 普通下载
     * @param fileName
     * @return
     */
    @StandardCreateApi(summary = "文件普通下载", description = "文件普通下载")
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

    /**
     * 分片文件下载
     * @param fileName
     * @param range
     * @return
     */
    @StandardCreateApi(summary = "文件分片下载", description = "文件分片下载")
    @GetMapping("/download-chunk/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,
                                               @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        try {
            long fileSize = FileUtils.getFileSize(uploadDir, fileName);
            byte[] fileContent = fileStorageService.downloadFileRange(fileName, range);

            if (fileContent == null) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).body(null);
            }

            String fileType = "application/octet-stream";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, fileType);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length));

            // 无论是否有 range，都添加 Content-Range
            if (range != null) {
                headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + range.replace("bytes=", "") + "/" + fileSize);
            } else {
                // 当没有 range 时，返回完整文件的范围
                headers.add(HttpHeaders.CONTENT_RANGE, "bytes 0-" + (fileSize - 1) + "/" + fileSize);
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @StandardCreateApi(summary = "图片直链", description = "图片直链")
    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> previewFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileStorageService.downloadFile(fileName);
            // 使用 Apache Tika 来识别文件的 MIME 类型
            Tika tika = new Tika();
            String fileType = tika.detect(fileContent); // 获取文件的 MIME 类型
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(fileType)) // 设置正确的文件类型
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 返回404
        }
    }
}

