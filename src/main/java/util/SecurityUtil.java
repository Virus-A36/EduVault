package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class SecurityUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;

    private static final String KEY_FILE = "vault.key";
    private static final String DEFAULT_FALLBACK_KEY = "G6vX8q2K9t1P4w7Z0y3B5c8N2m4V6x8L1j3H5f7D9s0=";
    private static volatile SecretKeySpec cachedKey = null;

    private SecurityUtil() {
    }

    // Hash Password using PBKDF2
    public static String hash(char[] password) throws Exception {

        if (password == null || password.length == 0) {
            throw new IllegalArgumentException(
                    "Password cannot be empty!"
            );
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            byte[] hashed = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt)
                    + ":"
                    + Base64.getEncoder().encodeToString(hashed);

        } finally {
            spec.clearPassword();
        }
    }

    // Verify Password
    public static boolean verify(
            char[] password,
            String stored
    ) throws Exception {

        if (password == null || stored == null) {
            return false;
        }

        String[] parts = stored.split(":", -1);

        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);

        if (iterations < 1 || iterations > 1000000) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expected = Base64.getDecoder().decode(parts[2]);

        if (salt.length != SALT_LENGTH ||
                expected.length != KEY_LENGTH / 8) {
            return false;
        }

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                expected.length * 8
        );

        try {
            byte[] actual = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            return MessageDigest.isEqual(expected, actual);

        } finally {
            spec.clearPassword();
        }
    }

    // Get AES Encryption Key from Environment, vault.key, or default fallback
    private static synchronized SecretKeySpec key() {

        if (cachedKey != null) {
            return cachedKey;
        }

        // 1. Check environment variable
        String value = System.getenv("EDUVAULT_AES_KEY");

        // 2. If not in environment, check or create local vault.key file
        if (value == null || value.trim().isEmpty()) {
            java.io.File file = new java.io.File(KEY_FILE);
            if (file.exists()) {
                try {
                    value = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).trim();
                } catch (Exception e) {
                    System.err.println("Could not read " + KEY_FILE + ": " + e.getMessage());
                }
            }

            // 3. Fallback to default persistent 32-byte key
            if (value == null || value.trim().isEmpty()) {
                value = DEFAULT_FALLBACK_KEY;
                try {
                    java.nio.file.Files.write(file.toPath(), value.getBytes(StandardCharsets.UTF_8));
                    System.out.println(">> Initialized AES key in: " + KEY_FILE);
                } catch (Exception ignored) {
                }
            }
        }

        byte[] bytes = Base64.getDecoder().decode(value);

        if (bytes.length != 32) {
            bytes = Base64.getDecoder().decode(DEFAULT_FALLBACK_KEY);
        }

        cachedKey = new SecretKeySpec(bytes, "AES");
        return cachedKey;
    }

    public static synchronized String getActiveKeyBase64() {
        SecretKeySpec k = key();
        return Base64.getEncoder().encodeToString(k.getEncoded());
    }

    public static synchronized void setCustomKey(String base64Key) throws Exception {
        if (base64Key == null || base64Key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty.");
        }
        byte[] bytes = Base64.getDecoder().decode(base64Key.trim());
        if (bytes.length != 32) {
            throw new IllegalArgumentException("AES key must be exactly 32 bytes (256 bits).");
        }
        cachedKey = new SecretKeySpec(bytes, "AES");
        java.io.File file = new java.io.File(KEY_FILE);
        java.nio.file.Files.write(file.toPath(), base64Key.trim().getBytes(StandardCharsets.UTF_8));
    }

    public static synchronized String generateNewKey() throws Exception {
        byte[] generated = new byte[32];
        RANDOM.nextBytes(generated);
        String base64 = Base64.getEncoder().encodeToString(generated);
        setCustomKey(base64);
        return base64;
    }

    // Encrypt Text using AES-256-GCM
    public static String encrypt(String text) throws Exception {

        if (text == null) {
            throw new IllegalArgumentException(
                    "Text cannot be null!"
            );
        }

        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key(),
                new GCMParameterSpec(128, iv)
        );

        byte[] encrypted = cipher.doFinal(
                text.getBytes(StandardCharsets.UTF_8)
        );

        byte[] result = Arrays.copyOf(
                iv,
                iv.length + encrypted.length
        );

        System.arraycopy(
                encrypted,
                0,
                result,
                iv.length,
                encrypted.length
        );

        return Base64.getEncoder().encodeToString(result);
    }

    // Decrypt Text
    public static String decrypt(String value) throws Exception {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Encrypted value cannot be empty!"
            );
        }

        byte[] all = Base64.getDecoder().decode(value);

        if (all.length < IV_LENGTH + 16) {
            throw new IllegalArgumentException(
                    "Invalid encrypted data!"
            );
        }

        byte[] iv = Arrays.copyOfRange(
                all,
                0,
                IV_LENGTH
        );

        byte[] encrypted = Arrays.copyOfRange(
                all,
                IV_LENGTH,
                all.length
        );

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                key(),
                new GCMParameterSpec(128, iv)
        );

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(
                decrypted,
                StandardCharsets.UTF_8
        );
    }
}
