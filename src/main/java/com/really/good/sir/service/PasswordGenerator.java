package com.really.good.sir.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordGenerator {

    public String hashPassword() {
        try {
            Random random = new Random();
            int number = 10000000 + random.nextInt(90000000);
            String password = String.valueOf(number);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
