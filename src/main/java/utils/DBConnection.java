package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3307;
    private static final String DATABASE = "gestion_utilisateur";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useUnicode=true"
                    + "&characterEncoding=utf8"
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC";

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        connect();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        connect();
        return connection;
    }

    public static boolean verifyConnection() {
        try {
            Connection connection = getInstance().getConnection();
            return connection != null && connection.isValid(2);
        } catch (SQLException e) {
            System.err.println("Database connection verification failed: " + e.getMessage());
            return false;
        }
    }

    public static String getDatabaseUrl() {
        return URL;
    }

    public static void closeConnection() {
        DBConnection instance = getInstance();
        if (instance.connection == null) {
            return;
        }

        try {
            if (!instance.connection.isClosed()) {
                instance.connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error while closing database connection: " + e.getMessage());
        }
    }
}
