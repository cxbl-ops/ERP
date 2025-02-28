package com.erp.E02.utils;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {
    public static String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return System.currentTimeMillis() + "-" + UUID.randomUUID() + "-" + extension;
    }

    public static boolean isImage(MultipartFile file) throws IOException {
        // 获取文件的MIME类型
        String contentType = file.getContentType();
        // 判断文件类型
        // 如果是图片类型，可以直接返回 true
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        // 否则，使用文件内容来验证是否为图片
        byte[] bytes = file.getBytes();
        String fileSignature = getFileSignature(bytes);
        return fileSignature.startsWith("FFD8FF") || fileSignature.startsWith("89504E47"); // JPEG or PNG
    }

    private static String getFileSignature(byte[] bytes) {
        // 获取文件的前几个字节并转换为十六进制字符串
        StringBuilder signature = new StringBuilder();
        for (int i = 0; i < Math.min(8, bytes.length); i++) {
            signature.append(String.format("%02X", bytes[i]));
        }
        return signature.toString();
    }

    public static long getFileSize(String uploadDir, String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        return Files.size(path);
    }
}
