package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
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
                System.out.println("✅ Database connection established successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            connection = null;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking connection status: " + e.getMessage());
            connection = null;
        }
        return connection;
    }

    public static boolean verifyConnection() {
        try {
            Connection connection = getInstance().getConnection();
            if (connection == null) {
                System.err.println("❌ Database connection is null");
                return false;
            }
            boolean isValid = connection.isValid(2);
            if (isValid) {
                System.out.println("✅ Database connection is valid");
            } else {
                System.err.println("❌ Database connection is not valid");
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("❌ Database connection verification failed: " + e.getMessage());
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
                System.out.println("✅ Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error while closing database connection: " + e.getMessage());
        }
    }
}
