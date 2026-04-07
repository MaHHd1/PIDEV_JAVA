package main;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        System.out.println("Database URL: " + DBConnection.getDatabaseUrl());

        if (DBConnection.verifyConnection()) {
            System.out.println("Database connection is valid.");
            runSqlTest();
            printUtilisateurTable();
        } else {
            System.out.println("Database connection failed.");
        }

        DBConnection.closeConnection();
    }

    private static void runSqlTest() {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DATABASE() AS db_name")) {

            if (resultSet.next()) {
                System.out.println("Connected database: " + resultSet.getString("db_name"));
            } else {
                System.out.println("SQL test ran, but no result was returned.");
            }
        } catch (SQLException e) {
            System.out.println("SQL test failed: " + e.getMessage());
        }
    }

    private static void printUtilisateurTable() {
        String sql = "SELECT * FROM utilisateur";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            boolean hasRows = false;

            System.out.println("Content of table 'utilisateur':");

            while (resultSet.next()) {
                hasRows = true;
                StringBuilder row = new StringBuilder();

                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        row.append(" | ");
                    }
                    row.append(metaData.getColumnLabel(i))
                            .append("=")
                            .append(resultSet.getObject(i));
                }

                System.out.println(row);
            }

            if (!hasRows) {
                System.out.println("The table 'utilisateur' is empty.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to read table 'utilisateur': " + e.getMessage());
        }
    }
}
