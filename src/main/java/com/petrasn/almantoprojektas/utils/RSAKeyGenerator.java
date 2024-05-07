package com.petrasn.almantoprojektas.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeyGenerator {
    private BigInteger p;
    private BigInteger q;
    private String password;
    private String userName;
    private KeyPair keyPair;

    public RSAKeyGenerator() {

    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public RSAKeyGenerator(String password, String userName) {
        this.password = password;
        this.userName = userName;
        apskaiciuoti();


    }

    private void apskaiciuoti() {
        byte[] out = sekosGeneravimas(2048, password, userName);
       // System.out.println(out.length);
      byte[] subarray1  = new byte[128];
      byte[] subarray2  = new byte[128];
      System.arraycopy(out,0,subarray1,0,128);
        subarray1[0] |= (byte) 0b10000000;
        subarray1[127] |= 0b00000001;
        p = new BigInteger(1,subarray1);

        System.arraycopy(out,128,subarray2,0,128);
        subarray2[0] |= (byte) 0b10000000;
        subarray2[127] |= 0b00000001;
        q = new BigInteger(1,subarray2);
        p=gautiArtimiausiaPirmini(p);
        q = gautiArtimiausiaPirmini(q);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger("65537");
        BigInteger d = e.modInverse(phi);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(n, e);
            PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
            RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(n, d);
            PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }

    }

    private BigInteger gautiArtimiausiaPirmini(BigInteger skaicius){
        while(!skaicius.isProbablePrime(100)){
          skaicius =  skaicius.add(BigInteger.TWO);
        }
        return skaicius;
    }


    private byte[] sekosGeneravimas(int sekosIlgis, String password, String userName) {
        try {


            byte[] salt =userName.getBytes(Charset.forName("ASCII"));
            //byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7}; // Druskos reikšmė
            int iterations = 1000; // Iteracijų skaičius
            int keyLength = sekosIlgis; // Reikšmės ilgis bitais
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }




}
