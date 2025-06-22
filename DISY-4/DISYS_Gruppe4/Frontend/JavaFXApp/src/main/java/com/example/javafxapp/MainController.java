package com.example.javafxapp;// JavaFX-Bibliotheken für GUI, Hintergrundprozesse und Plattformzugriffe
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

// Hauptklasse, die JavaFX-Anwendung darstellt
public class MainController extends Application {

    // UI-Elemente (per FXML eingebunden)
    @FXML
    private Button gatherDataBtn; // Button zum Auslösen der Datenerfassung

    @FXML
    private Button downloadBtn;   // Button zum Herunterladen der Rechnung

    @FXML
    private TextField CIDField1;  // Eingabefeld für Kundennummer (zum Erstellen)

    @FXML
    private TextField CIDField2;  // Eingabefeld für Kundennummer (zum Herunterladen)

    @FXML
    private Label InvoiceInformation; // Label zur Anzeige von Antworttexten

    @FXML
    private TextArea InvoiceCreationresponse; // TextArea für Antwortdetails

    // Initialisierer – wird nach FXML-Laden aufgerufen
    @FXML
    public void initialize() {
        // Button-Aktionen setzen (Lambda-Ausdrücke)
        gatherDataBtn.setOnAction(event -> gatherData(CIDField1.getText()));
        downloadBtn.setOnAction(event -> InvoiceDownload(CIDField2.getText()));
    }

    // Funktion zum Versenden einer POST-Anfrage zum Erstellen einer Rechnung
    @FXML
    private void gatherData(String id) {
        // Validierung: nur Zahlen erlaubt und nicht leer
        if (!id.isEmpty() && Pattern.matches("^[0-9]+$", id)) {

            // Erstelle einen neuen HTTP-Client und Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                    .POST(HttpRequest.BodyPublishers.ofString(id))
                    .build();

            // Asynchroner Aufruf
            try {
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(body -> Platform.runLater(() -> InvoiceInformation.setText(body)))
                        .join(); // wartet auf Beendigung
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id.isEmpty()) {
            InvoiceInformation.setText("Please enter a Customer ID!");
        } else {
            InvoiceInformation.setText("Only numbers are valid!");
        }
    }

    // Funktion zum Abrufen und Öffnen einer PDF-Rechnung
    private void InvoiceDownload(String id) {
        if (!id.isEmpty() && Pattern.matches("^[0-9]+$", id)) {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    // Antwort verarbeiten
                    String responseBody = response.body();
                    Platform.runLater(() -> InvoiceCreationresponse.setText(responseBody));
                    InvoiceCreationresponse.setVisible(true);

                    // Versuche die PDF lokal zu öffnen
                    try {
                        File file = new File(".\\Backend\\FileStorage\\" + id + ".pdf");
                        HostServices hostServices = getHostServices();
                        hostServices.showDocument(file.getAbsolutePath());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // Fehlerhafte HTTP-Antwort
                    InvoiceCreationresponse.setText("HTTP Error: " + statusCode);
                    InvoiceCreationresponse.setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id.isEmpty()) {
            InvoiceCreationresponse.setText("Please enter a Customer ID!");
            InvoiceCreationresponse.setVisible(true);

        } else {
            InvoiceCreationresponse.setText("Only numbers are valid!");
            InvoiceCreationresponse.setVisible(true);
        }
    }

    // Diese Methode ist erforderlich, weil die Klasse Application erweitert,
    // wird hier aber nicht genutzt, da MainApplication den Start übernimmt.
    @Override
    public void start(Stage stage) throws Exception {
        // leer – wird hier nicht genutzt
    }
}
