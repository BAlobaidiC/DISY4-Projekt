package com.example.javafxapp;// Die wichtigsten JavaFX-Bibliotheken für eine Anwendung werden importiert
import javafx.application.Application;     // Basisklasse für jede JavaFX-Anwendung
import javafx.fxml.FXMLLoader;             // Lädt FXML-Dateien (GUI-Layout)
import javafx.scene.Scene;                 // Repräsentiert den sichtbaren Bereich (Fensterinhalt)
import javafx.stage.Stage;                 // Das Hauptfenster der Anwendung

import java.io.IOException;                // Wird benötigt, falls das Laden der FXML-Datei fehlschlägt

// Die Hauptklasse der Anwendung, die von Application erbt
public class MainApplication extends Application {

    // Diese Methode wird beim Start der Anwendung automatisch von JavaFX aufgerufen
    @Override
    public void start(Stage stage) throws IOException {
        // Ein FXMLLoader wird erstellt, der die "main-view.fxml"-Datei lädt
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        // Es wird eine Scene mit dem geladenen Layout erstellt – Größe: 800x600 Pixel
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Die Szene wird dem Hauptfenster (Stage) zugewiesen
        stage.setScene(scene);

        // Das Fenster wird angezeigt
        stage.show();
    }

    // Die main()-Methode ist der Einstiegspunkt der Anwendung
    public static void main(String[] args) {
        // Mit launch() wird die JavaFX-Anwendung gestartet,
        // dadurch wird die start()-Methode aufgerufen
        launch();
    }
}
