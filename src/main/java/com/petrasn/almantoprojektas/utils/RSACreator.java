package com.petrasn.almantoprojektas.utils;

import java.security.*;
import java.util.Base64;

public class RSACreator {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String publicKeyString;
    private String privateKeyString;
    public RSACreator() {
    sukurtiRaktus();
    }

    private void sukurtiRaktus() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
             privateKey = keyPair.getPrivate() ;
             publicKeyString= Base64.getEncoder().encodeToString(publicKey.getEncoded());
             privateKeyString =Base64.getEncoder().encodeToString(privateKey.getEncoded());
            } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

}
