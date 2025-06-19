package org.example.Service;

import org.example.Data.Station;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private final static String POSTGRESQL = "postgresql";
    private final static String HOST = "localhost";
    private final static int PORT = 5433;
    private final static String DB_NAME = "disysdb";
    private final static String USERNAME = "disysuser";
    private final static String PASSWORD = "disyspw";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }

    public static List<Station> select() {

        String query = "SELECT * FROM station";

        List<Station> stations = new ArrayList<>();

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {

            while(rs.next()) {
                int id = rs.getInt("id");
                String db_url = rs.getString("db_url");
                float lat = rs.getFloat("lat");
                float lng = rs.getFloat("lng");

                Station station = new Station(id, db_url, lat, lng);
                stations.add(station);

                System.out.printf(station.toString());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return stations;
    }

    private static String getUrl() {

        return String.format(
                "jdbc:%s://%s:%s/%s?user=%s&password=%s", POSTGRESQL, HOST, PORT, DB_NAME, USERNAME, PASSWORD
        );
    }
}
