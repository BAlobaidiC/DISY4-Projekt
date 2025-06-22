package com.example.javafxapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 450, 380); // Angepasste Größe gemäß FXML

            MainController controller = fxmlLoader.getController();
            if (controller == null) {
                throw new IOException("Controller konnte nicht initialisiert werden");
            }
            controller.setHostServices(getHostServices());

            stage.setTitle("Rechnungsverwaltung");
            stage.setResizable(false);  // Verhindert Größenänderung
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showError("Anwendungsfehler", "Die Anwendung konnte nicht gestartet werden", e);
            Platform.exit();
        }
    }

    private void showError(String title, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText("Fehlerdetails: " + e.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}