package com.carrental.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveFile(MultipartFile file);

    void deleteFile(String fileUrl);

    String generatePresignedUrl(String fileUrl);
}
