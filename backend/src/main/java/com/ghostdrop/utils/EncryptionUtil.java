package com.ghostdrop.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.*;

public class EncryptionUtil {

    private static final String AES = "AES";

    private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";

    private static final int IV_SIZE = 16;

    private static final int KEY_SIZE = 16;

    /**
     * Encrypts the given byte array using AES/CBC/PKCS5Padding with the provided encryption key.
     *
     * @param data      The byte array to be encrypted.
     * @param secretKey The encryption key (as byte array).
     * @return The encrypted data (IV + ciphertext).
     * @throws Exception If an error occurs during encryption.
     */
    public static byte[] encrypt(byte[] data, byte[] secretKey) throws Exception {
        // generate random IV.
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // initialize Cipher with AES/CBC/PKCS5Padding.
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // encrypt the data.
        byte[] encryptedData = cipher.doFinal(data);

        // combine IV and encrypted data into a single byte array
        byte[] ivAndEncryptedData = new byte[IV_SIZE + encryptedData.length];
        System.arraycopy(iv, 0, ivAndEncryptedData, 0, IV_SIZE);
        System.arraycopy(encryptedData, 0, ivAndEncryptedData, IV_SIZE, encryptedData.length);

        return ivAndEncryptedData;
    }

    /**
     * Decrypts the given byte array (which contains both IV and ciphertext) using AES/CBC/PKCS5Padding.
     *
     * @param encryptedData The byte array containing IV and ciphertext.
     * @param secretKey     The decryption key (as byte array).
     * @return The decrypted data (plaintext).
     * @throws Exception If an error occurs during decryption.
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] secretKey) throws Exception {
        // extract the IV from the beginning of the encrypted data.
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE);

        // extract the actual ciphertext.
        byte[] ciphertext = new byte[encryptedData.length - IV_SIZE];
        System.arraycopy(encryptedData, IV_SIZE, ciphertext, 0, ciphertext.length);

        // initialize Cipher with AES/CBC/PKCS5Padding.
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, AES);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // decrypt the ciphertext.
        return cipher.doFinal(ciphertext);
    }

    /**
     * Converts the unique code to a 128-bit AES key.
     *
     * @param uniqueCode The unique code.
     * @return The AES key as a byte array.
     */
    public static byte[] generateKeyFromUniqueCode(String uniqueCode) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(uniqueCode.getBytes());

        byte[] aesKey = new byte[KEY_SIZE];
        System.arraycopy(key, 0, aesKey, 0, KEY_SIZE);

        return aesKey;
    }
}

