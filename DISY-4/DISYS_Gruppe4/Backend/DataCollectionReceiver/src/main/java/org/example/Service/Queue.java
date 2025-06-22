package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Queue {
    private final static String DCD_DCR = "DCD_DCR";
    private final static String SDC_DCR = "SDC_DCR";
    private final static String DCR_PG = "DCR_PG";
    private final static String HOST = "localhost";
    private final static int PORT = 5672; // Standardport für RabbitMQ
    private final static String USERNAME = "guest";
    private final static String PASSWORD = "guest";
    
    private int expectedCount;
    private int receivedCount = 0;

    private static ConnectionFactory factory;

    public Queue() {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
    }

// Diese Klasse empfängt Nachrichten von zwei verschiedenen Queues (DCD_DCR und SDC_DCR), verarbeitet sie und sendet das Ergebnis an eine dritte Queue (DCR_PG).
// Sie dient als Bindeglied zwischen verschiedenen Komponenten, die über RabbitMQ kommunizieren.
    private final static String DCD_DCR1 = "DCD_DCR";  // Queue für den DataCollectionDispatcher, der die erwarteten Nachrichten sendet
    private final static String SDC_DCR1 = "SDC_DCR"; // Queue für den StationDataCollector, der die tatsächlichen Nachrichten sendet
    private final static String DCR_PG1 = "DCR_PG";// Queue für den PDFGenerator, der die berechneten Gesamtsummen empfängt
    private final static String HOST1 = "localhost";
    private final static int PORT1 = 30003;
    private int expectedCount1;   // Wie viele Nachrichten erwartet werden (kommt von DCD_DCR)
    private int receivedCount1 = 0; //wiw viele Nachrichten empfangen wurden (kommt von SDC_DCR)

    private static ConnectionFactory factory1; // Factory für RabbitMQ-Verbindungen, um Nachrichten zu empfangen und zu senden

    // Konstruktor, der die Verbindungseinstellungen für RabbitMQ initialisiert.
    

// Diese Methode startet den Prozess, indem sie zuerst die erwarteten Nachrichten von der DataCollectionDispatcher-Queue (DCD_DCR) absorbiert
    public void absorbExpectedMessages() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(DCD_DCR, false, false, false, null);

        System.out.println(" [*] Waiting for messages from DataCollectionDispatcher. Press CTRL+C to exit");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from the DataCollectionDispatcher: '" + message + "' " + LocalTime.now());


            String[] keyValuePairs = message.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("count")) {
                        expectedCount = Integer.parseInt(value);
                    }
                }
            }
            System.out.println("expCount " + expectedCount);
        };

        channel.basicConsume(DCD_DCR, true, deliverCallback, consumerTag -> {
        });
    }
// Diese Methode absorbiert die tatsächlichen Nachrichten von der StationDataCollector-Queue (SDC_DCR).
    public void absorbActualMessages() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(SDC_DCR, false, false, false, null);

        System.out.println(" [*] Waiting for messages from the StationDataCollector. Press CTRL+C to exit");

        List<String> receivedMessages = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from the StationDataCollector: '" + message + "' " + LocalTime.now());
            receivedMessages.add(message);
            receivedCount++;
            // Check if the number of received messages from CONSUME2 matches the expected number
            if (expectedCount == receivedCount) {
                System.out.println("got this " + receivedMessages);
                try {
                    System.out.println("calculating " + receivedMessages);
                    String customerTotal = calculateTotal(receivedMessages);
                    send(customerTotal);
                    receivedCount = 0;
                    receivedMessages.clear();
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        channel.basicConsume(SDC_DCR, true, deliverCallback, consumerTag -> {
        });
    }

    // Diese Methode sendet die berechnete Gesamtsumme an die PDFGenerator-Queue (DCR_PG).
    private void send(String customerData) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            System.out.println("Publishing " + customerData);
            channel.queueDeclare(DCR_PG, false, false, false, null);
            channel.basicPublish("", DCR_PG, null, customerData.getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent: '" + customerData + "' to PDFGenerator");
        }
    }

    // Diese Methode berechnet die Gesamtsumme der kWh aus den empfangenen Nachrichten.
    public String calculateTotal(List<String> receivedMessages) {
        float totalKWH = 0;
        String id = null;

        for (String message : receivedMessages) {
            String[] keyValuePairs = message.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("id")) {
                        id = value;
                    } else if (key.equals("kwh")) {
                        String cleanedValue = value.replaceAll(",", "."); // Remove commas
                        totalKWH += Float.valueOf(cleanedValue);
                    }
                }
            }
        }
        if (id != null) {
            System.out.println(totalKWH);
            return "id=" + id + "&totalKWH=" + totalKWH;
        }
        return null;
    }

}