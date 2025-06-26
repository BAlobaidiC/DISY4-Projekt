package at.technikum_wien.gui;

import at.technikum_wien.gui.model.CurrentPercentage;
import at.technikum_wien.gui.model.UsageHour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Hauptklasse der JavaFX-Anwendung für das Energy Community Dashboard
 */
public class Main extends Application {
    // Label für die Anzeige des Community Pool Prozentsatzes
    private final Label communityPoolLabel = new Label("Community Pool: ");

    // Label für die Anzeige des Netzanteil-Prozentsatzes
    private final Label gridPortionLabel = new Label("Grid Portion: ");

    // DatePicker für das Startdatum, initialisiert mit aktuellem Datum
    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());

    // Dropdown-Liste für die Startzeit
    private final ComboBox<String> startTimePicker = new ComboBox<>();

    // DatePicker für das Enddatum, initialisiert mit aktuellem Datum
    private final DatePicker endDatePicker = new DatePicker(LocalDate.now());

    // Dropdown-Liste für die Endzeit
    private final ComboBox<String> endTimePicker = new ComboBox<>();

    // Labels für die Anzeige der verschiedenen Energiewerte
    private final Label totalProducedLabel = new Label("Community produced: ");
    private final Label totalUsedLabel = new Label("Community used: ");
    private final Label totalGridLabel = new Label("Grid used: ");

    // ObjectMapper für JSON-Verarbeitung, konfiguriert für Datum/Zeit-Handhabung
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // HTTP-Client für API-Anfragen
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {
        // Setzt den Fenstertitel
        primaryStage.setTitle("Energy Community Dashboard");

        // Erstellt das Hauptlayout-Grid
        GridPane grid = new GridPane();
        grid.setVgap(8);        // Vertikaler Abstand zwischen Elementen
        grid.setHgap(10);       // Horizontaler Abstand zwischen Elementen
        grid.setPadding(new Insets(10)); // Rand um das gesamte Grid

        // Konfiguriert drei Spalten mit gleicher Breite
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(34);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        // Füllt die Zeitauswahl-Dropdowns mit Stunden (00:00-23:00)
        for (int h = 0; h < 24; h++) {
            String hour = String.format("%02d:00", h);
            startTimePicker.getItems().add(hour);
            endTimePicker.getItems().add(hour);
        }
        // Setzt Standardwerte für die Zeitauswahl
        startTimePicker.setValue("14:00");
        endTimePicker.setValue("14:00");

        // Erstellt und konfiguriert den Aktualisieren-Button
        Button refreshBtn = new Button("refresh");
        refreshBtn.setOnAction(e -> fetchCurrent()); // Aktion bei Klick

        // Fügt Elemente zum Grid hinzu
        // Parameter: Element, Spalte, Zeile, Spaltenbreite, Zeilenhöhe
        grid.add(communityPoolLabel, 0, 0, 2, 1);
        grid.add(gridPortionLabel, 0, 1, 2, 1);
        grid.add(refreshBtn, 0, 2);

        // Fügt Zeitauswahl-Elemente hinzu
        grid.add(new Label("Start"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(startTimePicker, 2, 3);

        grid.add(new Label("End"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(endTimePicker, 2, 4);

        // Erstellt und konfiguriert den Daten-Anzeige-Button
        Button showDataBtn = new Button("show data");
        showDataBtn.setOnAction(e -> fetchHistorical());
        grid.add(showDataBtn, 0, 5);

        // Fügt Labels für Gesamtwerte hinzu
        grid.add(totalProducedLabel, 0, 6, 3, 1);
        grid.add(totalUsedLabel, 0, 7, 3, 1);
        grid.add(totalGridLabel, 0, 8, 3, 1);

        // Richtet Buttons links aus
        GridPane.setHalignment(refreshBtn, HPos.LEFT);
        GridPane.setHalignment(showDataBtn, HPos.LEFT);
        grid.setAlignment(Pos.TOP_LEFT);

        // Erstellt und zeigt das Fenster
        primaryStage.setScene(new Scene(grid, 340, 280));
        primaryStage.show();

        // Lädt initial aktuelle Daten
        fetchCurrent();
    }

    /**
     * Holt aktuelle Verbrauchsdaten vom Server
     */
    private void fetchCurrent() {
        try {
            // Erstellt und sendet HTTP-Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            // Verarbeitet die Antwort
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            CurrentPercentage cp = mapper.readValue(response.body(), CurrentPercentage.class);

            // Aktualisiert die Labels mit den neuen Werten
            communityPoolLabel.setText(String.format("Community Pool: %.2f%% used", cp.getCommunityDepleted()));
            gridPortionLabel.setText(String.format("Grid Portion: %.2f%%", cp.getGridPortion()));
        } catch (Exception e) {
            // Fehlerbehandlung
            communityPoolLabel.setText("Failed to fetch current data");
            gridPortionLabel.setText("");
        }
    }

    /**
     * Holt historische Daten vom Server für den ausgewählten Zeitraum
     */
    private void fetchHistorical() {
        try {
            // Erstellt DateTime-Objekte aus den ausgewählten Werten
            LocalDateTime start = LocalDateTime.of(startDatePicker.getValue(),
                    LocalTime.parse(startTimePicker.getValue()));
            LocalDateTime endRaw = LocalDateTime.of(endDatePicker.getValue(),
                    LocalTime.parse(endTimePicker.getValue()));
            // Setzt End-Zeit auf Ende der Stunde
            LocalDateTime end = endRaw.withMinute(59).withSecond(59);

            // Formatiert Datum/Zeit für die URL
            String formattedStart = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedEnd = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Erstellt und sendet HTTP-Request
            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s",
                    formattedStart, formattedEnd);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verarbeitet die JSON-Antwort
            List<UsageHour> usageData = mapper.readValue(response.body(), new TypeReference<>() {});

            // Berechnet Summen
            double totalProduced = usageData.stream().mapToDouble(UsageHour::getCommunityProduced).sum();
            double totalUsed = usageData.stream().mapToDouble(UsageHour::getCommunityUsed).sum();
            double totalGrid = usageData.stream().mapToDouble(UsageHour::getGridUsed).sum();

            // Aktualisiert die Labels
            totalProducedLabel.setText(String.format("Community produced: %.3f kWh", totalProduced));
            totalUsedLabel.setText(String.format("Community used: %.3f kWh", totalUsed));
            totalGridLabel.setText(String.format("Grid used: %.3f kWh", totalGrid));
        } catch (Exception e) {
            // Fehlerbehandlung
            totalProducedLabel.setText("Error fetching data");
            totalUsedLabel.setText("");
            totalGridLabel.setText("");
            e.printStackTrace();
        }
    }

    // Startet die JavaFX-Anwendung
    public static void main(String[] args) {
        launch();
    }
}
