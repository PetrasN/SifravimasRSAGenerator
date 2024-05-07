module com.petrasn.almantoprojektas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires  static lombok;
    requires jbcrypt;
    requires protobuf.java;
    requires java.desktop;
    opens com.petrasn.almantoprojektas to javafx.fxml;
    exports com.petrasn.almantoprojektas;
    exports com.petrasn.almantoprojektas.controllers;
    opens com.petrasn.almantoprojektas.controllers to javafx.fxml;
}