package service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OTPService {

    private static final OTPService INSTANCE = new OTPService();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int EXPIRY_SECONDS = 300; // 5 minutes

    private static class OTPEntry {
        final String code;
        final Instant expiresAt;

        OTPEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final Map<String, OTPEntry> otpStore = new ConcurrentHashMap<>();

    public static OTPService getInstance() {
        return INSTANCE;
    }

    public synchronized String generateOTP(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        // Generate 6-digit numeric code
        int num = 100000 + RANDOM.nextInt(900000);
        String code = String.valueOf(num);

        otpStore.put(email.toLowerCase().trim(), new OTPEntry(code, Instant.now().plusSeconds(EXPIRY_SECONDS)));

        System.out.println("==================================================");
        System.out.println(" [EduVault Verification Service]");
        System.out.println(" One-Time Password (OTP) for: " + email);
        System.out.println(" OTP Code: " + code);
        System.out.println(" Valid for 5 minutes.");
        System.out.println("==================================================");

        return code;
    }

    public synchronized boolean verifyOTP(String email, String inputCode) {
        if (email == null || inputCode == null) {
            return false;
        }

        String key = email.toLowerCase().trim();
        OTPEntry entry = otpStore.get(key);

        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            otpStore.remove(key);
            return false;
        }

        if (entry.code.equals(inputCode.trim())) {
            otpStore.remove(key);
            return true;
        }

        return false;
    }

    public synchronized boolean isCodeExpired(String email) {
        OTPEntry entry = otpStore.get(email.toLowerCase().trim());
        return entry == null || entry.isExpired();
    }
}
