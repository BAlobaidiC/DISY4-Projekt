package org.example;

import org.example.Service.Queue;
import java.util.concurrent.TimeoutException;
import java.io.IOException;

public class MainDCReceiver {

    public static void main(String[] args) throws IOException, TimeoutException {
// MainDCReceiver ist der Einstiegspunkt für den DataCollectionReceiver.
        Queue queue = new Queue(); // Erstellen einer Instanz der Queue-Klasse, die für die Kommunikation mit RabbitMQ verantwortlich ist
        DataCollectionReceiver dcr = new DataCollectionReceiver(queue);
        dcr.waitForData();  // Aufruf der Methode waitForData, die die erwarteten und tatsächlichen Nachrichten von den Queues absorbiert
    }
}