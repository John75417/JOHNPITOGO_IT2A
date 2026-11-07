
package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;
import com.scc.ui.ClinicMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            // Initialize database
            DatabaseInitializer.initialize();

            // Create application objects
            Scanner scanner = new Scanner(System.in);
            UserDao userDao = new UserDao();

            ClinicMenu clinicMenu =
                    new ClinicMenu(scanner, userDao);

            Authentication authentication =
                    new Authentication(userDao, scanner, clinicMenu);

            // Start application
            authentication.start();

        } catch (SQLException e) {

            System.out.println("\nUnable to start the application.");
            System.out.println("Database error details:");

            // Display exact error location
            e.printStackTrace();

        } catch (Exception e) {

            System.out.println("\nUnexpected application error:");
            e.printStackTrace();
        }
    }
}
