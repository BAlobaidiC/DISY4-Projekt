package org.example;

import org.example.Service.Queue;

import java.util.concurrent.TimeoutException;
import java.io.IOException;

public class MainSDCollector {

    public static void main(String[] args) throws IOException, TimeoutException {

        Queue q1 = new Queue();
        q1.receive();
    }
}