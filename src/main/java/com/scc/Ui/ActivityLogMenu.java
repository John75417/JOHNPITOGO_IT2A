
package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ActivityLogMenu {

    private final ActivityLogDao logDao;
    private final Scanner scanner;

    public ActivityLogMenu(
            ActivityLogDao logDao,
            Scanner scanner
    ) {
        this.logDao = logDao;
        this.scanner = scanner;
    }

    public void start() throws SQLException {

        boolean running = true;

        while (running) {

            System.out.println("\n===== SYSTEM AUDIT LOGS =====");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear Historical Logs");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" ->
                        displayLogs(logDao.getAllLogs());

                case "2" -> filterLogs();

                case "3" -> clearLogs();

                case "4" -> running = false;

                default ->
                        System.out.println("Invalid choice.");
            }
        }
    }

    private void filterLogs() throws SQLException {

        System.out.print("Enter User ID: ");

        try {
            int userId = Integer.parseInt(
                    scanner.nextLine().trim()
            );

            if (userId <= 0) {
                System.out.println("Invalid User ID.");
                return;
            }

            displayLogs(logDao.getLogsByUserId(userId));

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void clearLogs() throws SQLException {

        System.out.print(
                "WARNING: Delete all historical logs? (YES/NO): "
        );

        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Clear operation cancelled.");
            return;
        }

        if (logDao.clearAllLogs()) {
            System.out.println("Historical logs cleared successfully.");
        }
    }

    private void displayLogs(List<ActivityLog> logs) {

        System.out.println("\n================ SYSTEM AUDIT LOGS ================");

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION TYPE",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println(
                "---------------------------------------------------------------------------------------------"
        );

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        for (ActivityLog log : logs) {

            System.out.printf(
                    "%-5d | %-8d | %-18.18s | %-35.35s | %-20.20s%n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp()
            );
        }

        System.out.println(
                "\nTotal Records Displayed: " + logs.size()
        );
    }
}
