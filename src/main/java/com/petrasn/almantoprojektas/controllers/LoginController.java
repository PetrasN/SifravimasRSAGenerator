package com.petrasn.almantoprojektas.controllers;

import com.petrasn.almantoprojektas.utils.PasswordHashingAndChecking;
import com.petrasn.almantoprojektas.utils.StorageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Label lbAtention;
    @FXML
    public HBox hBox;
    @FXML
    private CheckBox cbPassword;

    @FXML
    private PasswordField pfPasword;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfUserName;
int count =0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbPassword.setVisible(false);
       tfPassword.setManaged(false);
        tfPassword.setVisible(false);
        tfPassword.managedProperty().bind(cbPassword.selectedProperty());
        tfPassword.visibleProperty().bind(cbPassword.selectedProperty());
        pfPasword.managedProperty().bind(cbPassword.selectedProperty().not());
        pfPasword.visibleProperty().bind(cbPassword.selectedProperty().not());
        tfPassword.textProperty().bindBidirectional(pfPasword.textProperty());
        StorageManager manager =new StorageManager();
        String userName = manager.getUserName();
        tfUserName.setText(userName);
        tfUserName.setEditable(false);

    }

    @FXML
    void onActionBtLogin(ActionEvent event) throws IOException {
        count++;
        if (count>2) cbPassword.setVisible(true);
        if(count>7) javafx.application.Platform.exit();
        String candidatePassword =pfPasword.getText();
       // String candidateUserName = tfUserName.getText();
        //Saugomas saugykla = new Saugykla();
        StorageManager manager =new StorageManager();
       // String userName = manager.getUserName();
        String hashedPassword=manager.getHashedPassword();
        boolean arGerasSlaptazodis= PasswordHashingAndChecking.checkPassword(candidatePassword,hashedPassword);
        //boolean argerasUserName= userName.equals(candidateUserName);
        //if(arGerasSlaptazodis&&argerasUserName){
        if(arGerasSlaptazodis){
            lbAtention.setText("OK");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/petrasn/almantoprojektas/view/pagrindinis-view.fxml"));

             Stage stage = (Stage) hBox.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            PagrindinisController controller = loader.getController();
            controller.init(candidatePassword);
            stage.show();

            stage.setTitle("Pagrindinis langas");


        } else{
            lbAtention.setText("Neteisingas  slaptažodis");
        }

    }
}