package com.pn.career.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pn.career.exceptions.InvalidMultipartFile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {

    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String publicId) throws IOException {
        String folder = "company";
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", folder
        );
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return uploadResult.get("url").toString();
    }
    public String uploadCvToCloudinary(MultipartFile file, String publicId) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "student"
        );
        Map uploadResult = cloudinary.uploader().upload(convFile, params);
        return uploadResult.get("url").toString();
    }
    //validate file pdf or image
    public boolean isFileValid(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("application/pdf") || contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

}
