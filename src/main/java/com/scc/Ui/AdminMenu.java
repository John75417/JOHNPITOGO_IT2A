
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final ActivityLogMenu activityLogMenu;
    private final Scanner scanner;

    public AdminMenu(
            UserDao userDao,
            ActivityLogDao logDao,
            Scanner scanner
    ) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;

        this.activityLogMenu =
                new ActivityLogMenu(logDao, scanner);
    }

    private int readIntInput(String prompt) {

        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(
                        scanner.nextLine().trim()
                );
            } catch (NumberFormatException e) {
                System.out.println("Enter numbers only.");
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

            String role = scanner.nextLine()
                    .trim().toUpperCase();

            switch (role) {
                case "0" -> {
                    return null;
                }
                case "1", "ADMINISTRATOR" -> {
                    return "ADMINISTRATOR";
                }
                case "2", "RECEPTIONIST" -> {
                    return "RECEPTIONIST";
                }
                case "3", "DOCTOR" -> {
                    return "DOCTOR";
                }
                default ->
                        System.out.println("Invalid role.");
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

            String status = scanner.nextLine()
                    .trim().toUpperCase();

            switch (status) {
                case "0" -> {
                    return null;
                }
                case "1", "ACTIVE" -> {
                    return "ACTIVE";
                }
                case "2", "PENDING" -> {
                    return "PENDING";
                }
                case "3", "REJECTED" -> {
                    return "REJECTED";
                }
                default ->
                        System.out.println("Invalid status.");
            }
        }
    }

    public void updateUser(User currentAdmin)
            throws SQLException {

        System.out.println(
                "\n===== UPDATE USER ROLE AND STATUS ====="
        );

        userDao.viewUsers();

        int targetUserId = readIntInput(
                "\nEnter User ID to update (0 to cancel): "
        );

        if (targetUserId <= 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (targetUserId == currentAdmin.getId()) {
            System.out.println(
                    "Modifying your own administrator account is restricted."
            );
            return;
        }

        User targetUser = userDao.getUserById(targetUserId);

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

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
                targetUserId,
                newRole,
                newStatus
        );

        if (updated) {

            logDao.logAction(
                    currentAdmin.getId(),
                    "USER_UPDATED",
                    "Admin " + currentAdmin.getId()
                            + " updated User " + targetUserId
                            + " role to " + newRole
                            + " and status to " + newStatus
            );

            System.out.println("\nUser updated successfully!");

            System.out.println("User ID: " + targetUserId);
            System.out.println("New Role: " + newRole);
            System.out.println("New Status: " + newStatus);

        } else {
            System.out.println("Failed to update user.");
        }

        userDao.viewUsers();
    }

    public void viewAuditLogs() throws SQLException {
        activityLogMenu.start();
    }
}
