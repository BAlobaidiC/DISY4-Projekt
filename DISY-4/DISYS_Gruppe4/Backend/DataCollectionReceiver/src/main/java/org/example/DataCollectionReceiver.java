package org.example;

import org.example.Service.Queue;
import java.util.concurrent.TimeoutException;
import java.io.IOException;

public class DataCollectionReceiver {

    private final Queue queue;


    public DataCollectionReceiver(Queue queue) {
        this.queue = queue;
    }

    public void waitForData() throws IOException, TimeoutException {

        this.queue.absorbExpectedMessages();
        this.queue.absorbActualMessages();
    }
}
