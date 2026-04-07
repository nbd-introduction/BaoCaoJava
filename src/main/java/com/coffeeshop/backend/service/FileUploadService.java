package com.coffeeshop.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Upload file ảnh → trả về đường dẫn để lưu DB
     * VD: /uploads/images/abc123.jpg
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // Kiểm tra định dạng
        String originalName = file.getOriginalFilename();
        if (originalName == null) return null;

        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh (jpg, png, gif, webp)!");
        }

        // Tạo tên file unique
        String fileName = UUID.randomUUID().toString() + ext;

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir, "images");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lưu file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/images/" + fileName;
    }

    /**
     * Upload file CV/PDF
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalName = file.getOriginalFilename();
        if (originalName == null) return null;

        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        String fileName = UUID.randomUUID().toString() + ext;

        Path uploadPath = Paths.get(uploadDir, "files");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/files/" + fileName;
    }

    /**
     * Xóa file cũ khi update ảnh mới
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) return;
        try {
            Path filePath = Paths.get(fileUrl.substring(1)); // bỏ dấu /
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Không thể xóa file: " + fileUrl);
        }
    }
}
