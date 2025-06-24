package com.example.javafxapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.application.HostServices;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;
import java.time.Duration;

public class MainController {
    private static final String API_URL_TEMPLATE = "http://localhost:8080/invoices/%s";
    private static final String PDF_PATH_TEMPLATE = ".\\Backend\\FileStorage\\%s.pdf";
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final String EMPTY_ID_MESSAGE = "Bitte geben Sie eine Kunden-ID ein!";
    private static final String INVALID_ID_MESSAGE = "Nur Zahlen sind erlaubt!";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    private HostServices hostServices;

    @FXML private Button gatherDataBtn;
    @FXML private Button downloadBtn;
    @FXML private TextField CIDField1;
    @FXML private TextField CIDField2;
    @FXML private Label InvoiceInformation;
    @FXML private TextArea InvoiceCreationresponse;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize() {
        gatherDataBtn.setOnAction(event -> gatherData(CIDField1.getText().trim()));
        downloadBtn.setOnAction(event -> downloadInvoice(CIDField2.getText().trim()));
    }

    private void gatherData(String customerId) {
        if (!isValidCustomerId(customerId)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(createApiUri(customerId))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        InvoiceInformation.setText(response.body());
                    } else {
                        InvoiceInformation.setText("Fehler beim Erstellen der Rechnung");
                    }
                    InvoiceInformation.setVisible(true);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        InvoiceInformation.setText("Verbindungsfehler zum Server");
                        InvoiceInformation.setVisible(true);
                    });
                    return null;
                });
    }

    private void downloadInvoice(String customerId) {
        if (!isValidCustomerId(customerId)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(createApiUri(customerId))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        InvoiceCreationresponse.setText("Rechnung gefunden");
                        InvoiceCreationresponse.setVisible(true);
                        openPdfFile(customerId);
                    } else if (response.statusCode() == 404) {
                        InvoiceCreationresponse.setText("Keine Rechnung gefunden");
                        InvoiceCreationresponse.setVisible(true);
                    } else {
                        InvoiceCreationresponse.setText("Fehler beim Abrufen der Rechnung");
                        InvoiceCreationresponse.setVisible(true);
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        InvoiceCreationresponse.setText("Verbindungsfehler zum Server");
                        InvoiceCreationresponse.setVisible(true);
                    });
                    return null;
                });
    }

    private boolean isValidCustomerId(String customerId) {
        if (customerId.isEmpty()) {
            showError(EMPTY_ID_MESSAGE);
            return false;
        }
        if (!NUMERIC_PATTERN.matcher(customerId).matches()) {
            showError(INVALID_ID_MESSAGE);
            return false;
        }
        return true;
    }

    private void openPdfFile(String customerId) {
        File pdfFile = new File(String.format(PDF_PATH_TEMPLATE, customerId));
        if (!pdfFile.exists()) {
            InvoiceCreationresponse.setText("PDF-Datei nicht gefunden");
            return;
        }
        
        if (hostServices != null) {
            hostServices.showDocument(pdfFile.getAbsolutePath());
        } else {
            InvoiceCreationresponse.setText("PDF kann nicht geöffnet werden");
        }
    }

    private URI createApiUri(String customerId) {
        return URI.create(String.format(API_URL_TEMPLATE, customerId));
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            InvoiceInformation.setText(message);
            InvoiceCreationresponse.setText(message);
            InvoiceCreationresponse.setVisible(true);
        });
    }
}