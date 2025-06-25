package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final String apiKey;
    private final String apiUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public WeatherService(Properties props) {
        this.apiKey = props.getProperty("weather.api.key");
        this.apiUrl = props.getProperty("weather.api.url");
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public double getSunIntensity() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/current.json?key=" + apiKey + "&q=auto:ip"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            
            double cloud = root.path("current").path("cloud").asDouble();
            int isDay = root.path("current").path("is_day").asInt();
            
            // Berechne Sonnenintensität (0.1 bis 1.0)
            if (isDay == 0) return 0.1; // Nacht
            return Math.max(0.1, 1.0 - (cloud / 100.0));
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Wetterdaten", e);
            return 0.5; // Standardwert bei Fehler
        }
    }
}