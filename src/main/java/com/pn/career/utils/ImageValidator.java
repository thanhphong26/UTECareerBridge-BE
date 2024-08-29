package com.pn.career.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageValidator {
    private static final List<String> ALLOWED_FORMATS = Arrays.asList("image/jpeg", "image/png");

    public static boolean isValidImage(MultipartFile file, int maxWidth, int maxHeight) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_FORMATS.contains(contentType)) {
                return false;
            }

            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return false;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            return width <= maxWidth && height <= maxHeight;
        } catch (IOException e) {
            return false;
        }
    }
}
