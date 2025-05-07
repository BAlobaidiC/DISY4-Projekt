package com.example.springapp.Queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Queue {

    private final static String SA_DCD = "SA_DCD";
    private final static String HOST = "localhost";
    private final static Integer PORT = 30003;

    public boolean send(String id) {

        boolean messageSent = false;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(SA_DCD, false, false, false, null);
            channel.basicPublish("", SA_DCD, null, id.getBytes());
            System.out.println(" [x] Sent '" + id + "'");
            messageSent = true;

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

        return messageSent;
    }
}
