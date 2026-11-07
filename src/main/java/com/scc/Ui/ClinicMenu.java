package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ClinicMenu {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AdminMenu adminMenu;

    public ClinicMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.adminMenu = new AdminMenu(userDao, scanner);
    }

    public void start(User user) throws SQLException {
        boolean running = true;

        while (running) {
            String role = user.getRoles().toUpperCase();

            switch (role) {
                case "ADMINISTRATOR" ->
                        running = showAdministratorMenu(user);
                case "RECEPTIONIST" ->
                        running = showReceptionistMenu();
                case "DOCTOR" ->
                        running = showDoctorMenu(user);
                default -> {
                    System.out.println("Unknown role.");
                    running = false;
                }
            }
        }
    }

    private boolean showAdministratorMenu(User currentAdmin)
            throws SQLException {

        System.out.println("\n===== ADMINISTRATOR MENU =====");
        System.out.println("1. View User Accounts");
        System.out.println("2. Activate User");
        System.out.println("3. Reject User");
        System.out.println("4. View Patients");
        System.out.println("5. View Appointments");
        System.out.println("6. View Patient Records");
        System.out.println("7. Update User Role and Status");
        System.out.println("8. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> userDao.viewUsers();

            case "2" -> {
                userDao.viewUsers();

                System.out.print(
                        "Enter User ID to activate (0 to cancel): ");

                try {
                    int id = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (id == 0) {
                        System.out.println("Action cancelled.");
                    } else if (userDao.activateUser(id)) {
                        System.out.println(
                                "User activated successfully!");
                    } else {
                        System.out.println(
                                "User not found or account is not PENDING.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Please enter a valid numeric ID.");
                }
            }

            case "3" -> {
                userDao.viewUsers();

                System.out.print(
                        "Enter User ID to reject (0 to cancel): ");

                try {
                    int id = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (id == 0) {
                        System.out.println("Action cancelled.");
                    } else if (userDao.rejectUser(id)) {
                        System.out.println(
                                "User rejected successfully!");
                    } else {
                        System.out.println(
                                "User not found or account is not PENDING.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Please enter a valid numeric ID.");
                }
            }

            case "4" -> userDao.viewPatients();

            case "5" -> userDao.viewAppointments();

            case "6" -> userDao.viewPatientRecords();

            case "7" -> adminMenu.updateUser(currentAdmin);

            case "8" -> {
                System.out.println("Logged out.");
                return false;
            }

            default -> System.out.println("Invalid choice.");
        }

        return true;
    }

    private boolean showReceptionistMenu() throws SQLException {
        System.out.println("\n===== RECEPTIONIST MENU =====");
        System.out.println("1. Add Patient");
        System.out.println("2. View Patients");
        System.out.println("3. Add Appointment");
        System.out.println("4. View Appointments");
        System.out.println("5. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Patient name: ");
                String name = scanner.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println(
                            "Patient name is required.");
                    break;
                }

                System.out.print("Patient age: ");

                try {
                    int age = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (age <= 0) {
                        System.out.println(
                                "Age must be greater than zero.");
                        break;
                    }

                    System.out.print("Patient contact number: ");
                    String contact = scanner.nextLine().trim();

                    userDao.addPatient(name, age, contact);

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid age.");
                }
            }

            case "2" -> userDao.viewPatients();

            case "3" -> {
                System.out.print("Patient ID (0 to cancel): ");

                try {
                    int patientId = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (patientId == 0) {
                        System.out.println("Action cancelled.");
                        break;
                    }

                    if (patientId < 0) {
                        System.out.println("Invalid patient ID.");
                        break;
                    }

                    System.out.print(
                            "Appointment date (YYYY-MM-DD): ");
                    String date = scanner.nextLine().trim();

                    System.out.print(
                            "Appointment time (HH:MM): ");
                    String time = scanner.nextLine().trim();

                    System.out.print("Reason for appointment: ");
                    String reason = scanner.nextLine().trim();

                    if (date.isEmpty() || time.isEmpty()
                            || reason.isEmpty()) {
                        System.out.println(
                                "Date, time, and reason are required.");
                        break;
                    }

                    userDao.addAppointment(
                            patientId, date, time, reason);

                } catch (NumberFormatException e) {
                    System.out.println(
                            "Please enter a valid patient ID.");
                }
            }

            case "4" -> userDao.viewAppointments();

            case "5" -> {
                System.out.println("Logged out.");
                return false;
            }

            default -> System.out.println("Invalid choice.");
        }

        return true;
    }

    private boolean showDoctorMenu(User currentDoctor)
            throws SQLException {

        System.out.println("\n===== DOCTOR MENU =====");
        System.out.println("1. View Appointments");
        System.out.println("2. View Patients");
        System.out.println("3. Add Patient Record");
        System.out.println("4. View Patient Records");
        System.out.println("5. Complete Appointment");
        System.out.println("6. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> userDao.viewAppointments();

            case "2" -> userDao.viewPatients();

            case "3" -> {
                System.out.print("Patient ID (0 to cancel): ");

                try {
                    int patientId = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (patientId == 0) {
                        System.out.println("Action cancelled.");
                        break;
                    }

                    if (patientId < 0) {
                        System.out.println("Invalid patient ID.");
                        break;
                    }

                    System.out.print("Diagnosis: ");
                    String diagnosis = scanner.nextLine().trim();

                    System.out.print("Treatment: ");
                    String treatment = scanner.nextLine().trim();

                    if (diagnosis.isEmpty()
                            || treatment.isEmpty()) {
                        System.out.println(
                                "Diagnosis and treatment are required.");
                        break;
                    }

                    userDao.addPatientRecord(
                            patientId,
                            currentDoctor.getId(),
                            diagnosis,
                            treatment
                    );

                } catch (NumberFormatException e) {
                    System.out.println(
                            "Please enter a valid patient ID.");
                }
            }

            case "4" -> userDao.viewPatientRecords();

            case "5" -> {
                System.out.print(
                        "Appointment ID (0 to cancel): ");

                try {
                    int appointmentId = Integer.parseInt(
                            scanner.nextLine().trim());

                    if (appointmentId == 0) {
                        System.out.println("Action cancelled.");
                        break;
                    }

                    if (userDao.completeAppointment(appointmentId)) {
                        System.out.println(
                                "Appointment marked as completed.");
                    } else {
                        System.out.println(
                                "Appointment not found or not scheduled.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println(
                            "Please enter a valid appointment ID.");
                }
            }

            case "6" -> {
                System.out.println("Logged out.");
                return false;
            }

            default -> System.out.println("Invalid choice.");
        }

        return true;
    }
}