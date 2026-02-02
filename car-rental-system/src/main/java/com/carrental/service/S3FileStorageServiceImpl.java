package com.carrental.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageServiceImpl implements FileStorageService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String saveFile(MultipartFile file) {
        log.info("Starting file upload to S3: {}", file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            // Upload file to S3 (Removed ACL as bucket ownership is enforced)
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            log.info("File uploaded successfully to S3: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        // Return filename (Key) only - Professional Practice
        return fileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        log.info("Deleting file from S3: {}", fileName);
        s3Client.deleteObject(bucketName, fileName);
    }

    @Override
    public String generatePresignedUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return fileUrl;
        }

        try {
            // Extract key from URL or use as is
            String fileName;
            if (fileUrl.startsWith("http")) {
                fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            } else {
                fileName = fileUrl;
            }

            // Generate public URL (since we set PublicRead ACL on upload)

            return s3Client.getUrl(bucketName, fileName).toString();

        } catch (Exception e) {
            log.error("Error generating S3 URL: {}", e.getMessage(), e);
            // Fallback to original URL if generation fails
            return fileUrl;
        }
    }
}
