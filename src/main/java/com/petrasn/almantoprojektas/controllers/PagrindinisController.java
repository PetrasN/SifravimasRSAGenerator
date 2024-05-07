package com.petrasn.almantoprojektas.controllers;

import com.petrasn.almantoprojektas.utils.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class PagrindinisController implements Initializable {

    @FXML
    public VBox checkBoxes;
    @FXML
    public TextField tfFilePatch;
    @FXML
    public TextField tfDirectoryPatch;
   @FXML
   public TextField tfFilePatchdecrypt;
    @FXML
    public TextField tfDirectoryPatchdecrypt;
    public TabPane tabPane;

    public ImageView imgVisitCard;
    public Label lbHelp;
    public MenuItem menuClose;


    private   String password;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lbHelp.setText("1. Tam, kad jums galėtų rašyti, jums reikia bendrinti savo vizitinė kortelę- savo prisijungimo"+
                " vardą ir viešą raktą, norėdami tai atlikti temkite su pele ikoną ir gautą failą paviešinkite"+
                " kitiems tinklo vartotojams.\n2. Tam, kad jūs galėtumete rašyti kitiems, jūs turite įkelti kitų kontaktus, "+
                "tai galima atlikti tempiant failą su kontaktais į kontaktų sarašą.\n3. Norėdami ištrinti nereikalingą kontaktą,"+
                " jį pažymėkite ir paspauskite mygtuką Vykdyti. ");
        checkBoxes.setSpacing(5);
        checkBoxes.setPadding(new Insets(10));
        tfFilePatch.setEditable(false);
        tfDirectoryPatch.setEditable(false);
        tfFilePatchdecrypt.setEditable(false);
        tfDirectoryPatchdecrypt.setEditable(false);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(5),imgVisitCard);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.play();


        imgVisitCard.setOnDragDetected(event -> {
            String userName = null;
            try(StorageManager manager =new StorageManager()){
                userName =  manager.getUserName();
                RSAKeyGenerator rsaKeyGenerator =new RSAKeyGenerator(password,userName);
               KeyPair keyPair= rsaKeyGenerator.getKeyPair();
               PublicKey publicKey = keyPair.getPublic();
                manager.createVisitCardfile(publicKey.getEncoded());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Dragboard db = imgVisitCard.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            // Nurodykite, kad tai yra failas
            File file =new File("storage/"+ userName+".visitCard");
            content.putFiles(Collections.singletonList(file));

            db.setContent(content);
            event.consume();

        });

    ikeltiKontaktuSarasa();

        checkBoxes.setOnDragOver(event -> {
            if (event.getGestureSource() != checkBoxes &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        checkBoxes.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".visitCard")) {
                        // Čia vykdomas veiksmas, kai tinkamas failas yra vilkamas ir paleidžiamas
                        try(StorageManager manager =new StorageManager()){
                            manager.loadFromVisitCard(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        success = true;
                        break;
                    }
                }

            }
            event.setDropCompleted(success);
            event.consume();
            ikeltiKontaktuSarasa();
        });

menuClose.setOnAction((ActionEvent event) -> {
    Platform.exit();
});

    }
 void init(String password){
        this.password =password;

 }
    private void ikeltiKontaktuSarasa() {
        checkBoxes.getChildren().clear();
        try(StorageManager manager = new StorageManager()){
         for(String userName:manager.getVisitCards().keySet()){
             CheckBox checkBox = new CheckBox(userName);
             checkBoxes.getChildren().add(checkBox);
         }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void onActionKontaktai(ActionEvent event)  {




        List<String> sarasas =getListCheckedBoxes();

            try(StorageManager manager =new StorageManager()){
                Map<String,byte[]> visitCards = manager.getVisitCards();
                for( String raktas:  sarasas){
                    visitCards.remove(raktas);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ikeltiKontaktuSarasa();


    }



    @FXML
    public void onChoosefile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bet koks failas", "*.*")
        );
        fileChooser.setTitle("Pasirinkite failą, kurį norėsite šifruoti");
        File selectFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (selectFile != null) {

        tfFilePatch.setText(selectFile.getAbsolutePath());
        } else{
            System.out.println("Failas nepasirinktas");
        }

    }


    @FXML
    public void onChooseDirektory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Pasirinkite direktoriją,");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(tabPane.getScene().getWindow());
        if (selectedDirectory != null) {


        tfDirectoryPatch.setText(selectedDirectory.toString());

        } else {
            System.out.println("Direktorija nepasirinkta.");
        }
    }



    @FXML
    public void onEncrypt(ActionEvent actionEvent) {
        // patikrinam ar netustas
        List<String> usersList= getListCheckedBoxes();
       String filePatch =tfFilePatch.getText();
       String directoryPatch = tfDirectoryPatch.getText();
       if(usersList.isEmpty()|| filePatch.isEmpty()||directoryPatch.isEmpty()){
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Dėmesio!");
           alert.setHeaderText(null);
           alert.setContentText("Abu laukai turi būti užpildyti ir pasirinktas bent vienas adresatas");
           alert.showAndWait();
       } else{
           CryptoManager cryptoManager = new CryptoManager(password);
           cryptoManager.sifruotiFaila(filePatch,directoryPatch,usersList);
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Informacija");
           alert.setHeaderText(null);
           alert.setContentText("Failas užšifruotas");
           alert.showAndWait();

       }

       java.awt.Toolkit.getDefaultToolkit().beep();
      //  System.out.print("\007");
    }
    private List<String> getListCheckedBoxes(){
        List<String> sarasas =new ArrayList<>();
        for (Node node :checkBoxes.getChildren() ){
            if (node instanceof CheckBox){
                CheckBox checkBox =(CheckBox) node;
                if (checkBox.isSelected())
                    sarasas.add(checkBox.getText());

            }
        }
        return sarasas;
    }

    @FXML
    public void onChoosefiledecrypt(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Šifruotas failas", "*.crypt")
        );
        fileChooser.setTitle("Pasirinkite failą, kurį norėsite dešifruoti");
        File selectFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (selectFile != null) {

            tfFilePatchdecrypt.setText(selectFile.getAbsolutePath());
        } else{
            System.out.println("Failas nepasirinktas");
        }
    }

    @FXML
    public void onChooseDirektorydecrypt(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Pasirinkite direktoriją,");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(tabPane.getScene().getWindow());
        if (selectedDirectory != null) {


            tfDirectoryPatchdecrypt.setText(selectedDirectory.toString());

        } else {
            System.out.println("Direktorija nepasirinkta.");
        }
    }

    @FXML
    public void onDecrypt(ActionEvent actionEvent) {

        String filePatch =tfFilePatchdecrypt.getText();
        String directoryPatch = tfDirectoryPatchdecrypt.getText();
        if( filePatch.isEmpty()||directoryPatch.isEmpty()){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dėmesio!");
            alert.setHeaderText(null);
            alert.setContentText("Abu laukai turi būti užpildyti!");

            alert.showAndWait();
        } else{
            CryptoManager cryptoManager =new  CryptoManager(password);
          String message= cryptoManager.defifruotiFaila(filePatch,directoryPatch);
            System.out.println(message);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacija!");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }

        java.awt.Toolkit.getDefaultToolkit().beep();
        //System.out.print("\007");

    }




    public void onKeistiRodyma(Event event) {
        ikeltiKontaktuSarasa();
//        tfDirectoryPatchdecrypt.setText("");
//        tfDirectoryPatch.setText("");
//        tfFilePatchdecrypt.setText("");
//        tfFilePatch.setText("");

        if(tabPane.getSelectionModel().getSelectedIndex()==2){
            checkBoxes.setDisable(true);
        } else{
            checkBoxes.setDisable(false);
        }
    }


    //perkelti i manageri


}
