package com.petrasn.almantoprojektas.utils;

import com.google.protobuf.ByteString;
import com.petrasn.almantoprojektas.CryptoProto.Crypto.Builder;
import com.petrasn.almantoprojektas.CryptoProto.Crypto;

import javax.crypto.*;

import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoManager {
    //private String fileName;
   // private String outputFilePath;
    //private byte[] encryptedFileContentWithSecretKey;
   //private byte[] fileContent;
    //private Map<String, byte[]> UsersWrapSecretKeyMap;
    private final String password;

    public CryptoManager(String password) {
 this.password = password;
    }

    public String defifruotiFaila(String filePath, String directoryPath){
        //Man reikia gauti savo varda
        //1 man reikia gauti  mano privateKey, kad galeciau unwrap Secret Key
        //
   String myUserName = null;
       try(StorageManager storageManager = new StorageManager()) {

           myUserName = storageManager.getUserName();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
        PrivateKey myPrivateKey = getPrivateKeyFromPassword();

        return loadDataFromFile( filePath,directoryPath,myUserName,myPrivateKey);
    }

    private String loadDataFromFile(String filePath, String directoryPath, String myUserName, PrivateKey myPrivateKey) {
      String message = "Failas dešifruotas";
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Builder cryptoBuilder = Crypto.newBuilder();
                Crypto crypto = cryptoBuilder.mergeFrom(fis).build();
                String senderName = crypto.getSenderName();
                String fileName = crypto.getFileName();
                String encryptedFilePath = directoryPath + "/" + fileName;
                byte[] encryptedContentWithSecretKey = crypto.getEncryptedContentWithSecretKey().toByteArray();
                byte[] encyptIVWithSenderPrivateKey = crypto.getEncyptIVWithSenderPrivateKey().toByteArray();

                PublicKey senderPublicKey = getPublicKeyFromStorage(senderName);
                if(senderPublicKey == null){
                    message = "Failo autoriaus nėra jūsų adresatų sąraše!";
                }else { //jei siuntėjo nėra sarasuose
                    byte[] ivBytes = decryptWithPublicKey(senderPublicKey, encyptIVWithSenderPrivateKey);
                    IvParameterSpec iv = new IvParameterSpec(ivBytes);
                    Map<String, ByteString> targetNameWrapSecretKeyMap = crypto.getTargetNameWrapSecretKeyMapMap();
                    // gauti  wrapedSecret key
                    ByteString wrappedsecretKeyByteString = targetNameWrapSecretKeyMap.get(myUserName);
                    if (wrappedsecretKeyByteString == null){

                        message ="Jūs nesate šio failo gavėjų sąraše!";
                    } else { // jei manes nera target sarasuose
                        byte[] wrappedsecretKey = targetNameWrapSecretKeyMap.get(myUserName).toByteArray();

                        SecretKey secretKey = KeyUnwrap(myPrivateKey, wrappedsecretKey);

                        CryptWithSecretKey cryptWithSecretKey = new CryptWithSecretKey(secretKey, iv);
                        byte[] fileContent = cryptWithSecretKey.Decrypt(encryptedContentWithSecretKey);
                        try (FileOutputStream fos = new FileOutputStream(encryptedFilePath)) {
                            fos.write(fileContent);

                        }

                    }
                }
                } catch(Exception e){
                    throw new RuntimeException(e);

                }

        }
        return message;
    }


    private PublicKey getPublicKeyFromStorage(String senderName) {
        try(StorageManager storageManager = new StorageManager()){
            byte[] sendersPublicKeyBytes=storageManager.getVisitCards().get(senderName);
            if(sendersPublicKeyBytes==null){
                return null;
            } else {
                return getPublicKeyFromPublicKeyBytes(sendersPublicKeyBytes);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sifruotiFaila(String filePath, String directoryPath, List<String> usersList) {

        //2 parametras
        String fileName = new File(filePath).getName();

        String fileNameWithoutExtension = fileName.replaceAll("\\.[^.]*$", "");
        // pavadinsim nauja faila nulinis parametras
        String outputFilePath = directoryPath + "/" + fileNameWithoutExtension + ".crypt";
         byte[] fileContent=null;
        try {
            fileContent = Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //1 parametras
        String senderName= null;
        Map<String, PublicKey> usersAndPublicKey = new HashMap<>();
        try (StorageManager storageManager = new StorageManager()) {
            senderName = storageManager.getUserName();
            Map<String, byte[]> visitCards = storageManager.getVisitCards();
            for (String raktas : usersList) {
                usersAndPublicKey.put(raktas,
                        getPublicKeyFromPublicKeyBytes(visitCards.get(raktas)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SecretKey secretKey = generateSecretKey();


        // 4 parametras
        IvParameterSpec iv= generateIv();


        try {
            fileContent = Files.readAllBytes(new File(filePath).toPath());
            System.out.println("cia1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3 parametras
         byte[] encryptedFileContentWithSecretKey=null;
        CryptWithSecretKey cryptWithSecretKey = new CryptWithSecretKey(secretKey, iv);
        try {
            encryptedFileContentWithSecretKey = cryptWithSecretKey.Encrypt(fileContent);
            System.out.println("cia2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



       byte[] ivBytes = iv.getIV();
        PrivateKey privateKey = getPrivateKeyFromPassword();
       // 4 parametras
    byte[]encodingIV=encryptWithPrivateKey(privateKey,iv.getIV());
// penktas parametras.
//usersAndPublicKey
        //secretKey

        Map<String, ByteString>targetNameWrapSecretKeyMap= new HashMap<>();
        for (Map.Entry<String, PublicKey> entry: usersAndPublicKey.entrySet())
              {
                 //byteArrayMap.put(entry.getKey(), entry.getValue().toByteArray());
            targetNameWrapSecretKeyMap.put(entry.getKey(),
                    ByteString.copyFrom(KeyWrap( entry.getValue(), secretKey)));
        }


       // turim visus parametrus.
        saveDataToFile(outputFilePath,senderName, fileName,encryptedFileContentWithSecretKey,
                encodingIV,targetNameWrapSecretKeyMap);
    }



    private PrivateKey getPrivateKeyFromPassword() {
        StorageManager storageManager = new StorageManager();
          String  userName = storageManager.getUserName();
        RSAKeyGenerator rsaKeyGenerator =new RSAKeyGenerator(password,userName);
        KeyPair keyPair= rsaKeyGenerator.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        return privateKey;
    }

    private PublicKey getPublicKeyFromPublicKeyBytes(byte[] publicKeyBytes) {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey generateSecretKey() {
        SecretKey secretKey = null;
        KeyGenerator keyGen = null;

        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return secretKey;
    }
    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return new IvParameterSpec(iv);
    }
    private byte[] encryptWithPrivateKey(PrivateKey privateKey, byte[] ivbytes) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(ivbytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
    private byte[] decryptWithPublicKey(PublicKey publicKey, byte[] encryptedIV){

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return  cipher.doFinal(encryptedIV);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
}
//PublicKey rsaPublic, SecretKey secretKey
    private byte[] KeyWrap(PublicKey rsaPublic, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, rsaPublic);
            byte[] wrappedKey = cipher.wrap(secretKey);
            return wrappedKey;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }


    }
//PrivateKey rsaPrivate, byte[] wrappedKey
   private SecretKey KeyUnwrap(PrivateKey rsaPrivate, byte[] wrappedKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, rsaPrivate);
            Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
            return (SecretKey) key;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    private void saveDataToFile(String outputFilePath, String senderName, String fileName,
                                byte[] encryptedFileContentWithSecretKey,
                                byte[] encodingIV, Map<String, ByteString> targetNameWrapSecretKeyMap) {
        Crypto crypto = Crypto.newBuilder()
                .setSenderName(senderName)
                .setFileName(fileName)
                .setEncryptedContentWithSecretKey(ByteString.copyFrom(encryptedFileContentWithSecretKey))
                .setEncyptIVWithSenderPrivateKey(ByteString.copyFrom(encodingIV))
                .putAllTargetNameWrapSecretKeyMap(targetNameWrapSecretKeyMap)
                .build();
       File file = new File(outputFilePath);
        try (FileOutputStream output = new FileOutputStream(file)) {
            crypto.writeTo(output);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
