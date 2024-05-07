package com.petrasn.almantoprojektas.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.DynamicMessage;
import com.petrasn.almantoprojektas.StorageProto.Storage;
import com.petrasn.almantoprojektas.StorageProto.Storage.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class StorageManager implements Closeable {
    @Setter
    @Getter
    private String userName;
    @Setter
    @Getter
    private String hashedPassword;

    @Setter
    @Getter
    private Map<String, byte[]> visitCards;

    private static final String FAILO_VARDAS = "storage/saugykla.dat";


    private File file;

    public StorageManager() {
        File directory = new File("storage");
        if (!directory.exists()) directory.mkdirs();
        file = new File(FAILO_VARDAS);
        visitCards = new HashMap<>();
        loadDataFromFile();
 //      visitCards.put("petras1@gmail.com", publicKey);
//        visitCards.put("petras2@gmail.com", publicKey);
//        visitCards.put("petras3@gmail.com", publicKey);
//        visitCards.put("petras4@gmail.com", publicKey);
//        visitCards.put("petras5@gmail.com", publicKey);
//        visitCards.put("petras6@gmail.com", publicKey);
//        visitCards.put("petras7@gmail.com", publicKey);
//        visitCards.put("petras8@gmail.com", publicKey);
//        visitCards.put("petras9@gmail.com", publicKey);
//        visitCards.put("petras10@gmail.com", publicKey);
//        visitCards.put("petras11@gmail.com", publicKey);
//        visitCards.put("petras12@gmail.com", publicKey);
//        visitCards.put("petras13@gmail.com", publicKey);
//        visitCards.put("petras14@gmail.com", publicKey);
//        visitCards.put("petras15@gmail.com", publicKey);
//        visitCards.put("petras16@gmail.com", publicKey);
    }

    private void loadDataFromFile() {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Builder storageBuilder = Storage.newBuilder();
                Storage storage = storageBuilder.mergeFrom(fis).build();
                userName = storage.getUserName();
                hashedPassword = storage.getHashedPassword();

                Map<String, ByteString> visitCardsByte = storage.getVisitCardsMap();
                if(visitCardsByte != null)
                visitCards = mapByteStringToMapByteArray(visitCardsByte);
//             if(visitCards.isEmpty()) {
//                 visitCards.put(userName,publicKey);
//             }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, byte[]> mapByteStringToMapByteArray(Map<String, ByteString> visitCardsByte) {
        Map<String, byte[]> byteArrayMap = new HashMap<>();
        for (Map.Entry<String, ByteString> entry : visitCardsByte.entrySet()) {
            byteArrayMap.put(entry.getKey(), entry.getValue().toByteArray());
        }
        return byteArrayMap;
    }

    @Override
    public void close() throws IOException {
        saveDataToFile();
    }

    private void saveDataToFile() {


        Map<String, ByteString> visitCardsByte = new HashMap<>();
        visitCardsByte = mapByteArrayToByteString(visitCards);
        Storage storage = Storage.newBuilder()
                .setUserName(userName)
                .setHashedPassword(hashedPassword)
                .putAllVisitCards(visitCardsByte)
                .build();
        try (FileOutputStream output = new FileOutputStream(file)) {
            storage.writeTo(output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Map<String, ByteString> mapByteArrayToByteString(Map<String, byte[]> visitCards) {
        Map<String, ByteString> byteStringMap = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : visitCards.entrySet()) {
            byteStringMap.put(entry.getKey(), ByteString.copyFrom(entry.getValue()));
        }
        return byteStringMap;
    }

    public void createVisitCardfile(byte[] publicKey) {
        String failepath ="storage/"+userName+".visitCard";

        //String failepath = directoryPath + "/" + userName + ".visitCard";

        Storage storage = Storage.newBuilder()
                .setUserName(userName)
                .setPublicKey(ByteString.copyFrom(publicKey))
                .build();
        try (FileOutputStream output = new FileOutputStream(failepath)) {
            storage.writeTo(output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadFromVisitCard(File file) {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Builder storageBuilder = Storage.newBuilder();
                Storage storage = storageBuilder.mergeFrom(fis).build();
                String userNameVC = storage.getUserName();
                byte[] publicKeyVC = storage.getPublicKey().toByteArray();
                visitCards.put(userNameVC, publicKeyVC);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
