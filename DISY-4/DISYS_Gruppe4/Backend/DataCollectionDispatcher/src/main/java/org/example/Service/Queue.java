package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Data.Station;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Queue {

    private final static String SA_DCD = "SA_DCD";
    private final static String DCD_SDC = "DCD_SDC";
    private final static String DCD_DCR = "DCD_DCR";
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

    public void receive(List<Station> stations) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(SA_DCD, false, false, false, null);
        System.out.println(" [*] Waiting for messages. Press CTRL+C to exit");
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' " + LocalTime.now());
            this.id = Integer.parseInt(message);

            try {
                send(stations);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(SA_DCD, true, deliverCallback, consumerTag -> {});
    }

    private void send(List<Station> stations) throws IOException, TimeoutException {
        int i = 0;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            
            channel.queueDeclare(DCD_SDC, false, false, false, null);

            for (Station s : stations) {
                String message = "db_url=" + s.getUrl() + "&id=" + this.id;
                channel.basicPublish("", DCD_SDC, null, message.getBytes(StandardCharsets.UTF_8));
                i++;
                System.out.println(" [" + i + "] has been sent '" + this.id + "' to the StationDataCollector");
            }
        }
        inform(i);
    }

    private void inform(int i) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(DCD_DCR, false, false, false, null);

            String message = "count=" + i + "&id=" + this.id;
            channel.basicPublish("", DCD_DCR, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x" + i + "] has informed the DataCollectionReceiver about the ID " + id);
        }
    }
}