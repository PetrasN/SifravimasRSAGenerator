package com.petrasn.almantoprojektas.controllers;

import com.petrasn.almantoprojektas.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ResourceBundle;

public class RegistracijaControllers implements Initializable {


    @FXML
    public Label lbHelp;
    @FXML
    private Label lbAtention;

    @FXML
    private PasswordField pfSlaptazodis;

    @FXML
    private TextField tfPastas;

    @FXML
    private TextField tfSlaptazodis;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    lbHelp.setText("Įveskite savo e-pašto adresą ir sukurkite lotynų abecele  slaptažodį, netrumpesnį nei 12 simbolių,"+
            " jame turi būti  bent viena didžioji raidė, mažoji raidė, skaičius bei specialus simbolis"+
            " iš pateikto sąrašo:  <>!@#$&*()_=+ \nSlaptažodį teks prisiminti!");
    }

    public void onActionRegistruotis(ActionEvent actionEvent) {
        var sPastas = tfPastas.getText();
        var sSlaptazodis = tfSlaptazodis.getText();
        var sPassword = pfSlaptazodis.getText();

        if(sPastas.trim().equals("") ||sSlaptazodis.trim().equals("") || sPassword.trim().equals("")  ){
            lbAtention.setText("Visi laukai turi būti užpildyti!");

        }

        else if(!Toolzz.isValidEmailAddress(sPastas)){
            lbAtention.setText("Pašto adreso sintaksė neteisinga!");
        } else if (!Toolzz.isStrengthPassword(sSlaptazodis)) {
            lbAtention.setText("Slaptažodis neatitinka kriterijų!");

        } else if(!sSlaptazodis.equals(sPassword)){
            lbAtention.setText("Slaptazodis ir pakartotas slaptazodis turi sutapti!");

        }
        else{

            lbAtention.setText("Viskas Ok");
            System.out.println("esam cia1");
            // 1.gaumame hashedpasworda
            String hashedPassword = PasswordHashingAndChecking.hashPassword(sPassword);
            System.out.println("esam cia2");
            // 2. Generuojame MasterKey ir Salt is passwordo
//            MasterKeyAndSaltGenerator generator = new MasterKeyAndSaltGenerator(sPassword);
//           SecretKey secretMasterKey = generator.getSecretMasterKey();
//            IvParameterSpec iv =generator.getIv();
//            System.out.println("esam cia3");
//            // 3 sukuriam RSA raktus
//            RSACreator rsaCreator= new  RSACreator();
//            PrivateKey privateKey=rsaCreator.getPrivateKey();
//            PublicKey publicKey = rsaCreator.getPublicKey();
//            System.out.println("esam cia4");
//
//            //4. Privatekey padarom stringa, Užšifruojame su Masterkey Private key  isejimas stringas.
//            String privateKeyBase64= RSAKeyTransform.PrivateKeyToStringBase64(privateKey);
//            CryptWithSecretKey cryptWithSecretKey = new CryptWithSecretKey(secretMasterKey,iv);
//
//            byte[] encryptedPrivateKeyBytes= null;
//            try{
//                encryptedPrivateKeyBytes = cryptWithSecretKey.Encrypt(privateKey.getEncoded());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
try(StorageManager manager = new StorageManager()){
    manager.setUserName(sPastas);
    manager.setHashedPassword(hashedPassword);


} catch (IOException e) {
    throw new RuntimeException(e);
}

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/petrasn/almantoprojektas/view/login-view.fxml"));

            Stage stage = (Stage) lbHelp.getScene().getWindow();
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.show();

            stage.setTitle("Prisijungimo langas");

        }

        }

}
