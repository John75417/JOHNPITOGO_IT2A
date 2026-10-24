
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;
    private final ClinicMenu clinicMenu;

    public Authentication(UserDao userDao, Scanner scanner,
                          ClinicMenu clinicMenu) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.clinicMenu = clinicMenu;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n===== CLINIC APPOINTMENT SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        login();
                        break;
                    case "2":
                        register();
                        break;
                    case "3":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void register() throws SQLException {
        System.out.println("\n===== REGISTER ACCOUNT =====");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("1. Administrator");
        System.out.println("2. Receptionist");
        System.out.println("3. Doctor");
        System.out.print("Choose role: ");

        String choice = scanner.nextLine().trim();
        String role;

        switch (choice) {
            case "1":
                role = "Administrator";
                break;
            case "2":
                role = "Receptionist";
                break;
            case "3":
                role = "Doctor";
                break;
            default:
                System.out.println("Invalid role selection.");
                return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password are required.");
            return;
        }

        try {
            boolean success = userDao.register(username, password, role);

            if (success) {
                System.out.println("Registration successful!");
                System.out.println("Selected role: " + role);
                System.out.println("Account status: PENDING");
                System.out.println("Wait for Administrator activation.");
            }
        } catch (SQLException e) {
            if (e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("unique")) {
                System.out.println("Username already exists.");
            } else {
                throw e;
            }
        }
    }

    private void login() throws SQLException {
        System.out.println("\n===== LOGIN =====");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println(
                    "Login failed. Check your credentials and account status."
            );
            System.out.println("Only ACTIVE accounts can log in.");
            return;
        }

        System.out.println("Login successful!");
        System.out.println("Welcome, " + user.getUsername());
        System.out.println("Role: " + user.getRoles());

        clinicMenu.start(user);
    }
}
