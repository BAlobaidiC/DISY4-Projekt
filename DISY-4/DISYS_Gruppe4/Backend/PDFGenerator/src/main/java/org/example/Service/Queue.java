package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.PdfGenerator.PDFGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Queue {

    private final static String DCR_PG = "DCR_PG";
    private final static String HOST = "localhost";
    private final static int PORT = 5672; // Angepasst auf Standard RabbitMQ Port
    private final static String USERNAME = "guest";
    private final static String PASSWORD = "guest";

    private float kwh;
    private int id;
    private static ConnectionFactory factory;

    public Queue() {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
    }

    // Rest des Codes bleibt unverändert...
}