package com.erp.E02.service.impl;

import com.erp.E02.service.FileStorageService;
import com.erp.E02.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.local.path}")
    private String uploadDir;

    /**
     * 普通上传
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String saveFileName = FileUtils.generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Paths.get(uploadDir, saveFileName);
        Files.createDirectories(path.getParent()); // 创建目录
        file.transferTo(path.toFile());
        //如果是图片返回直链
        if (FileUtils.isImage(file)) {
            return "http://localhost:8080/files/images/" + saveFileName;
        }
        //否则返回文件下载Url
        return "http://localhost:8080/files/download/" + saveFileName;
    }

    // 用一个Set来记录已上传的分片
    private Set<Integer> uploadedChunks = new HashSet<>();

    /**
     * 分片上传
     * @param file
     * @param chunk
     * @param totalChunks
     * @param fileName
     * @return
     * @throws IOException
     */
    @Override
    public String uploadFileChunk(MultipartFile file, int chunk, int totalChunks, String fileName) throws IOException {
        // 保存分片
        Path tempFilePath = Paths.get(uploadDir, fileName + ".part" + chunk);
        Files.write(tempFilePath, file.getBytes());
        // 记录上传的分片
        uploadedChunks.add(chunk);

        // 如果是最后一个分片，合并所有分片
        if (uploadedChunks.size() == totalChunks) {
            System.out.println("接收完成..."+fileName+" "+uploadedChunks.size());
            uploadedChunks.clear();
            //生成唯一文件名
            String uniqueFileName = FileUtils.generateUniqueFileName(fileName);
            mergeChunks(fileName, uniqueFileName, totalChunks);

            //如果是图片返回直链
            if (FileUtils.isImage(file)) {
                return "http://localhost:8080/files/images/" + uniqueFileName;
            }
            //否则返回文件下载Url
            return "http://localhost:8080/files/download/" + uniqueFileName;
        }
        return null;
    }

    /**
     * 普通下载
     * @param fileName
     * @return
     * @throws IOException
     */
    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        return Files.readAllBytes(path);
    }


    /**
     * 分片下载
     * @param fileName
     * @param range
     * @return
     * @throws IOException
     */
    @Override
    public byte[] downloadFileRange(String fileName, String range) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        long fileSize = Files.size(path);

        if (range == null) {
            // 如果没有 range 头，返回完整的文件
            return Files.readAllBytes(path);
        }

        // 解析 range 请求头
        String[] ranges = range.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = (ranges.length > 1 && !ranges[1].isEmpty()) ? Long.parseLong(ranges[1]) : fileSize - 1;

        if (start >= fileSize || end >= fileSize) {
            return null;  // 请求的范围无效
        }

        // 返回指定范围的字节数据
        byte[] content = new byte[(int) (end - start + 1)];
        try (var stream = Files.newInputStream(path)) {
            stream.skip(start);
            stream.read(content);
        }

        return content;
    }

    /**
     * 分片合并
     * @param originalFileName  原始分片文件名
     * @param uniqueFileName    最终合并保存的唯一文件名
     * @param totalChunks       总分片大小
     * @throws IOException
     */
    private void mergeChunks(String originalFileName, String uniqueFileName, int totalChunks) throws IOException {
        // 使用传入的目标文件名保存合并后的文件
        Path finalFilePath = Paths.get(uploadDir, uniqueFileName);
        System.out.println(Files.exists(finalFilePath.getParent()));

        // 合并所有分片
        try (var outputStream = Files.newOutputStream(finalFilePath)) {
            for (int i = 1; i <= totalChunks; i++) {
                // 使用传入的原始文件名来查找分片文件
                Path chunkPath = Paths.get(uploadDir, originalFileName + ".part" + i);
                byte[] chunkData = Files.readAllBytes(chunkPath);
                outputStream.write(chunkData);
                Files.delete(chunkPath); // 删除临时分片文件
            }
        }
    }

}

