package com.example.truststock.db;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    // hash using SHA-256(salt + password) -> hex/base64 string
    public static String hashPassword(String password, byte[] salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        md.update(password.getBytes("UTF-8"));
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    // convenience (salt in base64)
    public static String hashPassword(String password, String saltB64) throws Exception {
        return hashPassword(password, Base64.getDecoder().decode(saltB64));
    }
}
