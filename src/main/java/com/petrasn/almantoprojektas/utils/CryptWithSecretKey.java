package com.petrasn.almantoprojektas.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class CryptWithSecretKey {
    private final String algorithm;
    private final SecretKey secretKey;
    private final IvParameterSpec iv;


    public CryptWithSecretKey(SecretKey secretKey, IvParameterSpec iv) {
        this("AES/CBC/PKCS5Padding", secretKey, iv);
    }

    public CryptWithSecretKey(String algorithm, SecretKey secretKey, IvParameterSpec iv) {
        this.algorithm = algorithm;
        this.secretKey = secretKey;
        this.iv = iv;
    }

    public String Encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }
    public byte[] Encrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(input);
    }

    public String Decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
    public byte[] Decrypt(byte[] cipherBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(cipherBytes);
    }
}
