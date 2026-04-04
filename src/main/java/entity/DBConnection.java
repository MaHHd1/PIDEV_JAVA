package entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String DATABASE = "gestion_utilisateur";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Equivalent to:
    // mysql://root:@127.0.0.1:3306/gestion_utilisateur?serverVersion=mariadb-10.4.32&charset=utf8mb4
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?characterEncoding=utf8"
                    + "&useSSL=false"
                    + "&serverTimezone=UTC"
                    + "&allowPublicKeyRetrieval=true";

    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connection established successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }

        return connection;
    }

    public static boolean verifyConnection() {
        try {
            Connection currentConnection = getConnection();
            return currentConnection != null && currentConnection.isValid(2);
        } catch (SQLException e) {
            System.out.println("Database connection verification failed: " + e.getMessage());
            return false;
        }
    }

    public static String getDatabaseUrl() {
        return URL;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error while closing the database connection: " + e.getMessage());
        }
    }
}
