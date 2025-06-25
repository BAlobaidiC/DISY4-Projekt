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

public class Main extends Application {
    private final Label communityPoolLabel = new Label("Community Pool: ");
    private final Label gridPortionLabel = new Label("Grid Portion: ");

    private final DatePicker startDatePicker = new DatePicker(LocalDate.now());
    private final ComboBox<String> startTimePicker = new ComboBox<>();
    private final DatePicker endDatePicker = new DatePicker(LocalDate.now());
    private final ComboBox<String> endTimePicker = new ComboBox<>();

    private final Label totalProducedLabel = new Label("Community produced: ");
    private final Label totalUsedLabel = new Label("Community used: ");
    private final Label totalGridLabel = new Label("Grid used: ");

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Energy Community Dashboard");

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        // Column constraints for better alignment
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(34);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        for (int h = 0; h < 24; h++) {
            String hour = String.format("%02d:00", h);
            startTimePicker.getItems().add(hour);
            endTimePicker.getItems().add(hour);
        }
        startTimePicker.setValue("14:00");
        endTimePicker.setValue("14:00");

        Button refreshBtn = new Button("refresh");
        refreshBtn.setOnAction(e -> fetchCurrent());

        grid.add(communityPoolLabel, 0, 0, 2, 1);
        grid.add(gridPortionLabel, 0, 1, 2, 1);
        grid.add(refreshBtn, 0, 2);

        grid.add(new Label("Start"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(startTimePicker, 2, 3);

        grid.add(new Label("End"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(endTimePicker, 2, 4);

        Button showDataBtn = new Button("show data");
        showDataBtn.setOnAction(e -> fetchHistorical());
        grid.add(showDataBtn, 0, 5);

        grid.add(totalProducedLabel, 0, 6, 3, 1);
        grid.add(totalUsedLabel, 0, 7, 3, 1);
        grid.add(totalGridLabel, 0, 8, 3, 1);

        // Style tweaks to look closer to the image
        GridPane.setHalignment(refreshBtn, HPos.LEFT);
        GridPane.setHalignment(showDataBtn, HPos.LEFT);
        grid.setAlignment(Pos.TOP_LEFT);

        primaryStage.setScene(new Scene(grid, 340, 280)); // precise size
        primaryStage.show();

        fetchCurrent();
    }

    private void fetchCurrent() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            CurrentPercentage cp = mapper.readValue(response.body(), CurrentPercentage.class);
            communityPoolLabel.setText(String.format("Community Pool: %.2f%% used", cp.getCommunityDepleted()));
            gridPortionLabel.setText(String.format("Grid Portion: %.2f%%", cp.getGridPortion()));
        } catch (Exception e) {
            communityPoolLabel.setText("Failed to fetch current data");
            gridPortionLabel.setText("");
        }
    }

    private void fetchHistorical() {
        try {
            LocalDateTime start = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimePicker.getValue()));
            LocalDateTime endRaw = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimePicker.getValue()));
            LocalDateTime end = endRaw.withMinute(59).withSecond(59);

            String formattedStart = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedEnd = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", formattedStart, formattedEnd);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<UsageHour> usageData = mapper.readValue(response.body(), new TypeReference<>() {});

            double totalProduced = usageData.stream().mapToDouble(UsageHour::getCommunityProduced).sum();
            double totalUsed = usageData.stream().mapToDouble(UsageHour::getCommunityUsed).sum();
            double totalGrid = usageData.stream().mapToDouble(UsageHour::getGridUsed).sum();

            totalProducedLabel.setText(String.format("Community produced: %.3f kWh", totalProduced));
            totalUsedLabel.setText(String.format("Community used: %.3f kWh", totalUsed));
            totalGridLabel.setText(String.format("Grid used: %.3f kWh", totalGrid));
        } catch (Exception e) {
            totalProducedLabel.setText("Error fetching data");
            totalUsedLabel.setText("");
            totalGridLabel.setText("");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
