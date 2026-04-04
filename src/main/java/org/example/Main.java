package org.example;

import entity.DBConnection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Database URL: " + DBConnection.getDatabaseUrl());

        if (DBConnection.verifyConnection()) {
            System.out.println("Database connection is valid.");
        } else {
            System.out.println("Database connection failed.");
        }

        DBConnection.closeConnection();
    }
}
