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
    private final static int PORT = 30003;

    private int id;
    private static ConnectionFactory factory;

    public Queue() {

        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
    }

    public void receive() throws IOException, TimeoutException {

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(DCD_SDC, false, false, false, null);
        System.out.println(" [*] Waiting for messages. Press CTRL+C to exit");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' " + LocalTime.now());

            float kwh = getKWH(message);

            try {
                send(kwh);

            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(DCD_SDC, true, deliverCallback, consumerTag -> {});
    }
    private void send(float kwh) throws IOException, TimeoutException {

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {

            channel.queueDeclare(SDC_DCR, false, false, false, null);
            String message = String.format("id=%s&kwh=%.1f", id, kwh);
            channel.basicPublish("", SDC_DCR, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(message);
        }
    }

    public float getKWH(String message) {

        String[] parts = message.split("db_url=localhost:|&id=");
        int port = Integer.parseInt(parts[1]);
        this.id = Integer.parseInt(parts[2]);
        Database db = new Database(port);
        return db.select(id);
    }
}
