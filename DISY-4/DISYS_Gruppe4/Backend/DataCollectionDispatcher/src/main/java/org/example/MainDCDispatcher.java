package org.example;

import org.example.Service.Queue;
import org.example.Data.Station;
import org.example.Service.Database;

import java.util.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainDCDispatcher {

    public static void main(String[] args) throws IOException, TimeoutException {

        Database db = new Database();
        Queue queue = new Queue();

        DataCollectionDispatcher dcd = new DataCollectionDispatcher(db, queue);

        List<Station> stations = dcd.getDatabase();

        dcd.wait(stations);
    }
}