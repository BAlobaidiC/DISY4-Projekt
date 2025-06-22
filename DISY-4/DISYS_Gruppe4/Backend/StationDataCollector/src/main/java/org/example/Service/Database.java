package org.example.Service;

import java.sql.*;


public class Database {


    private final static String POSTGRESQL = "postgresql";
    private final static String HOST = "localhost";
    private static int PORT;
    private final static String DB_NAME = "stationdb";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "postgres";


    public Database(int port) {
        this.PORT = port;
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }


    public float select(int cid) {

        String query = "SELECT kwh FROM charge WHERE customer_id = ?";
        float kwh = 0;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);

        ) {
            ps.setInt(1, cid);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    kwh+= rs.getFloat("kwh");

                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return kwh;
    }

    private static String getUrl() {

        return String.format(
                "jdbc:%s://%s:%s/%s?user=%s&password=%s", POSTGRESQL, HOST, PORT, DB_NAME, USERNAME, PASSWORD
        );
    }
}