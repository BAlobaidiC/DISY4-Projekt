package org.example;

import org.example.Service.Queue;
import org.example.Data.Station;
import org.example.Service.Database;


import java.util.concurrent.TimeoutException;
import java.util.List;
import java.io.IOException;


public class DataCollectionDispatcher {

    private final Database database;
    private final Queue queue;


    public DataCollectionDispatcher(Database database, Queue queue) {
        this.database = database;
        this.queue = queue;
    }

    public List<Station> getDatabase() {
        return Database.select();
    }

    public void wait(List<Station> stations) throws IOException, TimeoutException {
        this.queue.receive(stations);
    }
}
