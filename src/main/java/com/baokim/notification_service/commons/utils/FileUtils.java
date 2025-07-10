package com.baokim.notification_service.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class FileUtils {

    @Value("${app.file-upload-dir}")
    private String uploadDir;

    public static File writeTempFile(byte[] data, String fileName) throws IOException {
        String suffix = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : ".tmp";
        File temp = Files.createTempFile("email-attach-", suffix).toFile();
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(data);
        }
        return temp;
    }

    public static void deleteTempFile(List<File> files) {
        if (files.isEmpty()) {
            return;
        }
        for (File file : files) {
            if (!file.delete()) {
                log.error("Không thể xoá file: {}",  file.getAbsolutePath());
            }
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {

        Path uploadDirPath = Paths.get(uploadDir);
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir, fileName);

        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }

    public static byte[] readFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static boolean deleteFiles(List<String> files) {
        try {
            for(String filePath : files) {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            }
            return true;
        } catch (IOException e) {
            log.error("Lỗi xoá file {}", e.getMessage(), e);
            return false;
        }
    }

}
