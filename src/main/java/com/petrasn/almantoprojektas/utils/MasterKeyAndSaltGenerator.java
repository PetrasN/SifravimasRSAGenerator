package com.petrasn.almantoprojektas.utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class MasterKeyAndSaltGenerator {
    private String masterKeyBase64;
    private  byte[] masterKey;
    SecretKey secretMasterKey;
    private byte[] salt;
     private IvParameterSpec iv;
     private KeySpec keySpec;
     private final int iterations = 10000;
     private  final int keyLength = 256;
    public MasterKeyAndSaltGenerator(String password) {

        salt = createSalt(password);
        iv = new IvParameterSpec(Arrays.copyOf(salt, 16));
        keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        masterKey = generateKey(keySpec);
        secretMasterKey=new SecretKeySpec( masterKey, "AES");
        masterKeyBase64 = Base64.getEncoder().encodeToString(masterKey);

    }

    private byte[] createSalt(String password) {
        try {
            // Naudojame SHA-256 maišos funkciją
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Konvertuojame maišos reikšmę į Base64 formatą
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private  byte[] generateKey(KeySpec keySpec)  {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(keySpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMasterKeyBase64() {
        return masterKeyBase64;
    }

    public byte[] getMasterKey() {
        return masterKey;
    }

    public byte[] getSalt() {
        return salt;
    }

    public SecretKey getSecretMasterKey() {
        return secretMasterKey;
    }

    public IvParameterSpec getIv() {
        return iv;
    }
}
