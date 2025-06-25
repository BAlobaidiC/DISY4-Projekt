package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.EnergyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

public class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private final WeatherService weatherService;
    private final Channel channel;
    private final String queueName;
    private final ObjectMapper mapper;
    private final Random random;

    public ProducerService() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/application.properties"));
        
        this.weatherService = new WeatherService(props);
        this.mapper = new ObjectMapper();
        this.random = new Random();
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(props.getProperty("rabbitmq.host"));
        factory.setPort(Integer.parseInt(props.getProperty("rabbitmq.port")));
        factory.setUsername(props.getProperty("rabbitmq.username"));
        factory.setPassword(props.getProperty("rabbitmq.password"));
        
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.queueName = props.getProperty("rabbitmq.queue");
        
        channel.queueDeclare(queueName, true, false, false, null);
    }

    public void startProducing() {
        while (true) {
            try {
                double sunIntensity = weatherService.getSunIntensity();
                // Basis-Produktion zwischen 0.002 und 0.005 kWh pro Minute
                double baseProduction = 0.002 + (random.nextDouble() * 0.003);
                // Multipliziere mit Sonnenintensität
                double actualProduction = baseProduction * sunIntensity;
                
                EnergyMessage message = new EnergyMessage(actualProduction, LocalDateTime.now());
                String jsonMessage = mapper.writeValueAsString(message);
                
                channel.basicPublish("", queueName, null, jsonMessage.getBytes());
                logger.info("Energie produziert: {} kWh", actualProduction);
                
                Thread.sleep(5000); // Warte 5 Sekunden
            } catch (Exception e) {
                logger.error("Fehler beim Senden der Nachricht", e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            ProducerService producer = new ProducerService();
            producer.startProducing();
        } catch (Exception e) {
            logger.error("Fehler beim Starten des Producers", e);
        }
    }
}