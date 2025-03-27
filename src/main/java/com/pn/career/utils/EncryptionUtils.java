package com.pn.career.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtils {
    @Value("${app.message.encryption.key}")
    private  String encryptionKey;

    private ThreadLocal<Cipher> encryptCipher;
    private ThreadLocal<Cipher> decryptCipher;

    @PostConstruct
    public void init() {
        encryptCipher = ThreadLocal.withInitial(() -> {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return cipher;
            } catch (Exception e) {
                throw new RuntimeException("Đã có lỗi không xác định. Vui lòng thử lại sau", e);
            }
        });

        decryptCipher = ThreadLocal.withInitial(() -> {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                return cipher;
            } catch (Exception e) {
                throw new RuntimeException("Đã có lỗi không xác định. Vui lòng thử lại sau", e);
            }
        });
    }

    public String encrypt(String content) {
        if (content == null) {
            return null;
        }
        try {
            byte[] encryptedBytes = encryptCipher.get().doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Đã có lỗi không xác định. Vui lòng thử lại sau", e);
        }
    }

    public String decrypt(String encryptedContent) {
        if (encryptedContent == null) {
            return null;
        }
        try {
            byte[] decryptedBytes = decryptCipher.get().doFinal(Base64.getDecoder().decode(encryptedContent));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Đã có lỗi không xác định. Vui lòng thử lại sau", e);
        }
    }
}
