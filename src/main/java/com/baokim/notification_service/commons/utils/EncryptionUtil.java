package com.baokim.notification_service.commons.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private static String SECRET_KEY;

    @Value("${app.encryption.secret-key}")
    public void setSecretKey(String secretKey) {
        EncryptionUtil.SECRET_KEY = secretKey;
    }

    private static final String AES_ALGORITHM = "AES";

    public static String encrypt(String data) {
        try {
            if (SECRET_KEY == null) {
                throw new IllegalStateException("Khóa bí mật chưa được khởi tạo");
            }
            SecretKeySpec key = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return data;
        }
    }

    public static String decrypt(String encryptedData){
        try {
            if (SECRET_KEY == null) {
                throw new IllegalStateException("Khóa bí mật chưa được khởi tạo");
            }
            SecretKeySpec key = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedData;
        }
    }
}
