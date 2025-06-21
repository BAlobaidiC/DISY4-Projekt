package org.example.Service;

import org.example.Data.Customer;
import java.sql.*;

public class Database {

    private final static String POSTGRESQL = "postgresql";
    private final static String HOST = "localhost";
    private final static int PORT = 5433; // angepasst
    private final static String DB_NAME = "disysdb"; // angepasst
    private final static String USERNAME = "disysuser"; // angepasst
    private final static String PASSWORD = "disyspw";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }

    public static Customer select(int sid) {
        String query = "SELECT * FROM customer WHERE id = ?;";
        Customer customer = null;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setInt(1, sid);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    if (id != 0 && !firstName.isEmpty() && !lastName.isEmpty()) {
                        customer = new Customer(id, firstName, lastName);
                        System.out.println(customer); // moved into block for cleaner output
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return customer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getUrl() {
        return String.format("jdbc:%s://%s:%d/%s?user=%s&password=%s",
                POSTGRESQL, HOST, PORT, DB_NAME, USERNAME, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung: " + e.getMessage());
            return false;
        }
    }

}
