package com.ghostdrop.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Utility class to provide encryption services to the files.
 * It uses the GCM along with AES encryption technique.
 *
 * @author Kasodariya Deep
 */
public class EncryptionUtil {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_SIZE = 12; // Recommended size for GCM IV is 12 bytes
    private static final int KEY_SIZE = 16; // AES-128, 16 bytes

    /**
     * Encrypts the given byte array using AES/GCM/NoPadding with the provided encryption key.
     *
     * @param data      The byte array to be encrypted.
     * @param secretKey The encryption key (as byte array).
     * @return The encrypted data (IV + ciphertext + authentication tag).
     * @throws Exception If an error occurs during encryption.
     */
    public static byte[] encrypt(byte[] data, byte[] secretKey) throws Exception {
        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Initialize Cipher with AES/GCM/NoPadding
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        // Encrypt the data
        byte[] encryptedData = cipher.doFinal(data);

        // Combine IV and encrypted data into a single byte array
        byte[] ivAndEncryptedData = new byte[IV_SIZE + encryptedData.length];
        System.arraycopy(iv, 0, ivAndEncryptedData, 0, IV_SIZE);
        System.arraycopy(encryptedData, 0, ivAndEncryptedData, IV_SIZE, encryptedData.length);

        return ivAndEncryptedData;
    }

    /**
     * Decrypts the given byte array (which contains both IV and ciphertext) using AES/GCM/NoPadding.
     *
     * @param encryptedData The byte array containing IV and ciphertext.
     * @param secretKey     The decryption key (as byte array).
     * @return The decrypted data (plaintext).
     * @throws Exception If an error occurs during decryption.
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] secretKey) throws Exception {
        // Extract the IV from the beginning of the encrypted data
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE);

        // Extract the actual ciphertext
        byte[] ciphertext = Arrays.copyOfRange(encryptedData, IV_SIZE, encryptedData.length);

        // Initialize Cipher with AES/GCM/NoPadding
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        // Decrypt the ciphertext
        return cipher.doFinal(ciphertext);
    }

    /**
     * Converts the unique code to a 128-bit AES key.
     *
     * @param uniqueCode The unique code.
     * @return The AES key as a byte array.
     * @throws Exception If an error occurs during key generation.
     */
    public static byte[] generateKeyFromUniqueCode(String uniqueCode) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(uniqueCode.getBytes());

        byte[] aesKey = new byte[KEY_SIZE];
        System.arraycopy(key, 0, aesKey, 0, KEY_SIZE);

        return aesKey;
    }
}
