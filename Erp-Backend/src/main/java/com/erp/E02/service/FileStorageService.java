package com.erp.E02.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String uploadFile(MultipartFile file) throws IOException;
    byte[] downloadFile(String fileName) throws IOException;

    String uploadFileChunk(MultipartFile file, int chunk, int totalChunks, String fileName) throws IOException;

    byte[] downloadFileRange(String fileName, String range) throws IOException;
}
