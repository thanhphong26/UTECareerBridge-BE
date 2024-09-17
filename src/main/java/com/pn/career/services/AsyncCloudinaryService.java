package com.pn.career.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Async
public class AsyncCloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file, String publicId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String folder = "company";
                Map<String, Object> uploadParams = Map.of(
                        "public_id", publicId,
                        "folder", folder
                );
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
                return uploadResult.get("url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        });
    }
    @Async
    public CompletableFuture<Void> deleteFile(String fileUrl) {
        return CompletableFuture.runAsync(() -> {
            try {
                String publicId = extractPublicIdFromUrl(fileUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file", e);
            }
        });
    }

    private String extractPublicIdFromUrl(String fileUrl) {
        // Giả định URL file của Cloudinary có dạng: https://res.cloudinary.com/{cloud_name}/image/upload/{folder}/{public_id}.{extension}
        // Hàm này sẽ trích xuất {public_id} từ URL file
        String[] parts = fileUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];  // Phần cuối của URL chứa public_id và extension
        return publicIdWithExtension.split("\\.")[0];  // Loại bỏ phần extension để lấy public_id
    }
}
