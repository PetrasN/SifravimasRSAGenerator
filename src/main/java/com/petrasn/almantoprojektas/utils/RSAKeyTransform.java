package com.petrasn.almantoprojektas.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeyTransform {
    public static String PublicKeyToStringBase64(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
    public static PublicKey StringBase64ToPublicKey(String publicKeyStringBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStringBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }
    public static String PrivateKeyToStringBase64(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }
    public static PrivateKey StringBase64ToPrivateKey(String privateKeyStringBase64) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStringBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }


}
