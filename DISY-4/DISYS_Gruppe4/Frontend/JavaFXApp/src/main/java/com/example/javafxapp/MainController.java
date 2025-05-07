package com.example.javafxapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainController {

    @FXML
    private Label outputLabel;

    @FXML
    protected void loadCurrentData() {
        String url = "http://localhost:8080/energy/current";
        String result = fetchData(url);
        outputLabel.setText("Aktuelle Daten:\n" + result);
    }

    @FXML
    protected void loadHistoricalData() {
        String url = "http://localhost:8080/energy/historical?start=2025-01-10T00:00&end=2025-01-10T23:00";
        String result = fetchData(url);
        outputLabel.setText("Historische Daten:\n" + result);
    }

    private String fetchData(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line).append("\n");
            }
            rd.close();
        } catch (Exception e) {
            return "Fehler beim Abrufen: " + e.getMessage();
        }
        return result.toString();
    }
}
