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
import java.util.function.Consumer;

public class MainController {
    private static final String API_URL_TEMPLATE = "http://localhost:8080/invoices/%s";
    private static final String PDF_PATH_TEMPLATE = ".\\Backend\\FileStorage\\%s.pdf";
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final String EMPTY_ID_MESSAGE = "Bitte geben Sie eine Kunden-ID ein!";
    private static final String INVALID_ID_MESSAGE = "Nur Zahlen sind erlaubt!";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    private HostServices hostServices;

    @FXML
    private Button gatherDataBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private TextField CIDField1;
    @FXML
    private TextField CIDField2;
    @FXML
    private Label InvoiceInformation;
    @FXML
    private TextArea InvoiceCreationresponse;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize() {
        gatherDataBtn.setOnAction(event -> gatherData(CIDField1.getText().trim()));
        downloadBtn.setOnAction(event -> downloadInvoice(CIDField2.getText().trim()));
    }

    @FXML
    private void gatherData(String customerId) {
        if (!isValidCustomerId(customerId)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(createApiUri(customerId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"customerId\": \"" + customerId + "\"}"))
                .build();

        sendAsyncRequest(request, response ->
                Platform.runLater(() -> {
                    InvoiceInformation.setText(response);
                    InvoiceInformation.setVisible(true);
                }));
    }

    private void downloadInvoice(String customerId) {
        if (!isValidCustomerId(customerId)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(createApiUri(customerId))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleDownloadResponse(response, customerId);
        } catch (Exception e) {
            handleError("Fehler beim Herunterladen der Rechnung", e);
        }
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

    private void handleDownloadResponse(HttpResponse<String> response, String customerId) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Platform.runLater(() -> {
                InvoiceCreationresponse.setText(response.body());
                InvoiceCreationresponse.setVisible(true);
                openPdfFile(customerId);
            });
        } else {
            showError("HTTP-Fehler: " + response.statusCode());
        }
    }

    private void openPdfFile(String customerId) {
        try {
            File pdfFile = new File(String.format(PDF_PATH_TEMPLATE, customerId));
            if (!pdfFile.exists()) {
                showError("PDF-Datei wurde nicht gefunden: " + pdfFile.getAbsolutePath());
                return;
            }
            if (hostServices != null) {
                hostServices.showDocument(pdfFile.getAbsolutePath());
            } else {
                showError("HostServices nicht verfügbar");
            }
        } catch (Exception e) {
            handleError("Fehler beim Öffnen der PDF-Datei", e);
        }
    }

    private void sendAsyncRequest(HttpRequest request, Consumer<String> responseHandler) {
        try {
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() >= 400) {
                            throw new RuntimeException("HTTP-Fehler: " + response.statusCode());
                        }
                        return response.body();
                    })
                    .thenAccept(responseHandler)
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> handleError("Fehler bei der Anfrage", new Exception(throwable)));
                        return null;
                    });
        } catch (Exception e) {
            handleError("Fehler beim Senden der Anfrage", e);
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

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        showError(message + ": " + e.getMessage());
    }
}