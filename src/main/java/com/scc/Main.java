
package com.scc;

import dao.DatabaseConnection;
import ui.Menu;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnection.initializeDatabase();

            System.out.println("Database connected successfully!");

            Menu menu = new Menu();
            menu.start();

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
