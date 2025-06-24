package org.example.Service;

import java.sql.*;

public class Database {
    private final static String POSTGRESQL = "postgresql";
    private final static String HOST = "localhost";
    private final static int PORT = 5433;          // Geändert zu 5433 für die zentrale DB
    private final static String DB_NAME = "disysdb"; // Geändert zu disysdb
    private final static String USERNAME = "disysuser"; // Geändert zu disysuser
    private final static String PASSWORD = "disyspw";   // Geändert zu disyspw

    public Database(int port) {
        // Port-Parameter wird nicht mehr benötigt, da wir immer Port 5433 verwenden
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }

    public float select(int cid) {
        String query = "SELECT SUM(kwh) as total_kwh FROM charge WHERE customer_id = ?";
        float kwh = 0;

        try (
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setInt(1, cid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kwh = rs.getFloat("total_kwh");
                }
            }

        } catch (SQLException e) {
            System.err.println("Datenbankfehler: " + e.getMessage());
        }

        return kwh;
    }

    private static String getUrl() {
        return String.format(
            "jdbc:%s://%s:%d/%s?user=%s&password=%s",
            POSTGRESQL, HOST, PORT, DB_NAME, USERNAME, PASSWORD
        );
    }
}