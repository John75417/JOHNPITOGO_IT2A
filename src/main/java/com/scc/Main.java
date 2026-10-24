
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
            DatabaseInitializer.initialize();

            Scanner scanner = new Scanner(System.in);
            UserDao userDao = new UserDao();

            ClinicMenu clinicMenu =
                    new ClinicMenu(scanner, userDao);

            Authentication authentication =
                    new Authentication(userDao, scanner, clinicMenu);

            authentication.start();

        } catch (SQLException e) {
            System.out.println("Unable to start the application.");
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
