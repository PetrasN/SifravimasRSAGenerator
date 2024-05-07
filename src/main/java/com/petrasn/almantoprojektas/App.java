package com.petrasn.almantoprojektas;

import com.petrasn.almantoprojektas.utils.StorageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        StorageManager manager =new StorageManager();
        String fxmlfileName="/com/petrasn/almantoprojektas/view/login-view.fxml";
        String title ="Prisijungimo puslapis";
        int width =400;
        int height= 250;
        if(manager.getHashedPassword() == null || manager.getHashedPassword().trim().isEmpty())
        {
             width =600;
             height= 400;
            fxmlfileName="/com/petrasn/almantoprojektas/view/registracija-view.fxml";
          title ="Registracijos puslapis";
        }


        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlfileName));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}