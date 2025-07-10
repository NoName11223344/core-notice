package com.baokim.notification_service.commons.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignUtil {

    // Hàm verify chữ ký
    public static boolean verifySignature(String plainText, String signature, String publicKeyPem) {
        try {
            // Chuyển đổi PEM public key sang PublicKey object
            PublicKey publicKey = getPublicKeyFromPem(publicKeyPem);

            // Khởi tạo Signature với SHA256withRSA
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);

            // Update dữ liệu cần verify
            verifier.update(plainText.getBytes("UTF-8"));

            // Decode base64 signature từ PHP
            byte[] signatureBytes = Base64.getDecoder().decode(signature);

            // Verify và trả về kết quả
            return verifier.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm chuyển đổi PEM public key sang PublicKey
    private static PublicKey getPublicKeyFromPem(String publicKeyPem) throws Exception {
        // Xóa header/footer và newline
        String cleanKey = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // Decode base64
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);

        // Tạo PublicKey từ bytes
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}