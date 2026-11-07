
package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.DatabaseConnection;
import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;

import com.scc.ui.Authentication;
import com.scc.ui.ClinicMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {

            // Initialize SQLite database
            DatabaseInitializer.initialize();

            System.out.println(
                    "SQLite database connected successfully!"
            );

            // Create application objects
            Scanner scanner = new Scanner(System.in);

            UserDao userDao = new UserDao();

            ActivityLogDao logDao =
                    new ActivityLogDao();

            // Create clinic menu
            ClinicMenu clinicMenu = new ClinicMenu(
                    scanner,
                    userDao,
                    logDao
            );

            // Create authentication
            Authentication authentication =
                    new Authentication(
                            userDao,
                            logDao,
                            scanner,
                            clinicMenu
                    );

            // Start application
            authentication.start();

        } catch (SQLException e) {

            System.out.println(
                    "\nUnable to start the application."
            );

            System.out.println(
                    "Database error details:"
            );

            e.printStackTrace();

        } catch (Exception e) {

            System.out.println(
                    "\nUnexpected application error:"
            );

            e.printStackTrace();
        }
    }
}
