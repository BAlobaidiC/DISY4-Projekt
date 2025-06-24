package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

public class Queue {
    private final static String DCD_SDC = "DCD_SDC";
    private final static String SDC_DCR = "SDC_DCR";
    private final static String HOST = "localhost";
    private final static int PORT = 5672;
    private final static String USERNAME = "guest";
    private final static String PASSWORD = "guest";

    private int id;
    private static ConnectionFactory factory;

    public Queue() {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
    }

    public void receive() throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null;
        
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(DCD_SDC, false, false, false, null);
            System.out.println(" [*] Warte auf Nachrichten. Drücken Sie STRG+C zum Beenden");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Empfangen '" + message + "' " + LocalTime.now());

                try {
                    float kwh = getKWH(message);
                    send(kwh);
                } catch (Exception e) {
                    System.err.println("Fehler bei der Verarbeitung der Nachricht: " + e.getMessage());
                }
            };

            channel.basicConsume(DCD_SDC, true, deliverCallback, consumerTag -> {});
            
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("Kritischer Fehler: " + e.getMessage());
        } finally {
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
                if (connection != null && connection.isOpen()) {
                    connection.close();
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }

    private void send(float kwh) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(SDC_DCR, false, false, false, null);
            String message = String.format("id=%d&kwh=%.1f", id, kwh);
            channel.basicPublish("", SDC_DCR, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Gesendet: " + message);
        }
    }

    private float getKWH(String message) {
        try {
            // Nachrichtenformat ändern zu einfacherem Format
            String[] parts = message.split("&id=");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Ungültiges Nachrichtenformat");
            }
            
            this.id = Integer.parseInt(parts[1]);
            
            // Verwende einen einzelnen Database-Instance ohne Port
            Database db = new Database(0); // Port wird nicht mehr verwendet
            return db.select(id);
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der Nachricht: " + e.getMessage());
            throw e;
        }
    }
}