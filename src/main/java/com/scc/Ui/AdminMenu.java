package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input! Please enter numbers only.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.println("\nAvailable Roles:");
            System.out.println("1. ADMINISTRATOR");
            System.out.println("2. RECEPTIONIST");
            System.out.println("3. DOCTOR");
            System.out.println("0. Cancel");
            System.out.print("Enter new role: ");

            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) return null;

            switch (role) {
                case "1", "ADMINISTRATOR" -> {
                    return "ADMINISTRATOR";
                }
                case "2", "RECEPTIONIST" -> {
                    return "RECEPTIONIST";
                }
                case "3", "DOCTOR" -> {
                    return "DOCTOR";
                }
                default -> System.out.println(
                        "Invalid role! Please select a valid role.");
            }
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.println("\nAvailable Statuses:");
            System.out.println("1. ACTIVE");
            System.out.println("2. PENDING");
            System.out.println("3. REJECTED");
            System.out.println("0. Cancel");
            System.out.print("Enter new status: ");

            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) return null;

            switch (status) {
                case "1", "ACTIVE" -> {
                    return "ACTIVE";
                }
                case "2", "PENDING" -> {
                    return "PENDING";
                }
                case "3", "REJECTED" -> {
                    return "REJECTED";
                }
                default -> System.out.println(
                        "Invalid status! Please select a valid status.");
            }
        }
    }

    public void updateUser(User currentAdmin) throws SQLException {
        System.out.println("\n===== UPDATE USER ROLE AND STATUS =====");

        userDao.viewUsers();

        int targetUserId = readIntInput(
                "\nEnter User ID to update (0 to cancel): ");

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (targetUserId < 0) {
            System.out.println("Invalid User ID. Update cancelled.");
            return;
        }

        if (targetUserId == currentAdmin.getId()) {
            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        User targetUser = userDao.getUserById(targetUserId);

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        System.out.println("\nSelected User:");
        System.out.println("ID: " + targetUser.getId());
        System.out.println("Username: " + targetUser.getUsername());
        System.out.println("Current Role: " + targetUser.getRoles());
        System.out.println("Current Status: " + targetUser.getStatus());

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId, newRole, newStatus);

        if (updated) {
            System.out.println("\nUser updated successfully!");
            System.out.println("Updated User ID: " + targetUserId);
            System.out.println("New Role: " + newRole);
            System.out.println("New Status: " + newStatus);
        } else {
            System.out.println(
                    "Failed to update user. User may no longer exist.");
        }

        System.out.println("\nUpdated User List:");
        userDao.viewUsers();
    }
}