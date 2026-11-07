
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ClinicMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final AdminMenu adminMenu;

    public ClinicMenu(
            Scanner scanner,
            UserDao userDao,
            ActivityLogDao logDao
    ) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.logDao = logDao;

        this.adminMenu = new AdminMenu(
                userDao, logDao, scanner
        );
    }

    public void start(User user) throws SQLException {

        boolean running = true;

        while (running) {

            String role = user.getRoles().toUpperCase();

            switch (role) {

                case "ADMINISTRATOR" ->
                        running = showAdministratorMenu(user);

                case "RECEPTIONIST" ->
                        running = showReceptionistMenu(user);

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
        System.out.println("8. View System Audit Logs");
        System.out.println("9. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {

            case "1" -> userDao.viewUsers();

            case "2" -> {
                userDao.viewUsers();

                int id = readId(
                        "Enter User ID to activate (0 to cancel): "
                );

                if (id == 0) {
                    System.out.println("Action cancelled.");
                } else if (userDao.activateUser(id)) {

                    logDao.logAction(
                            currentAdmin.getId(),
                            "USER_APPROVED",
                            "Admin " + currentAdmin.getId()
                                    + " approved account for User ID " + id
                    );

                    System.out.println(
                            "User activated successfully!"
                    );

                } else {
                    System.out.println(
                            "User not found or account is not PENDING."
                    );
                }
            }

            case "3" -> {
                userDao.viewUsers();

                int id = readId(
                        "Enter User ID to reject (0 to cancel): "
                );

                if (id == 0) {
                    System.out.println("Action cancelled.");
                } else if (userDao.rejectUser(id)) {

                    logDao.logAction(
                            currentAdmin.getId(),
                            "USER_REJECTED",
                            "Admin " + currentAdmin.getId()
                                    + " rejected User ID " + id
                    );

                    System.out.println(
                            "User rejected successfully!"
                    );

                } else {
                    System.out.println(
                            "User not found or account is not PENDING."
                    );
                }
            }

            case "4" -> userDao.viewPatients();

            case "5" -> userDao.viewAppointments();

            case "6" -> userDao.viewPatientRecords();

            case "7" -> adminMenu.updateUser(currentAdmin);

            case "8" -> adminMenu.viewAuditLogs();

            case "9" -> {
                System.out.println("Logged out.");
                return false;
            }

            default -> System.out.println("Invalid choice.");
        }

        return true;
    }

    private boolean showReceptionistMenu(User currentUser)
            throws SQLException {

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
                    System.out.println("Patient name is required.");
                    break;
                }

                int age = readId("Patient age: ");

                if (age <= 0) {
                    System.out.println("Invalid age.");
                    break;
                }

                System.out.print("Patient contact number: ");
                String contact = scanner.nextLine().trim();

                if (contact.isEmpty()) {
                    System.out.println("Contact is required.");
                    break;
                }

                if (userDao.addPatient(name, age, contact)) {

                    logDao.logAction(
                            currentUser.getId(),
                            "PATIENT_ADDED",
                            "Receptionist "
                                    + currentUser.getId()
                                    + " added patient " + name
                    );

                    System.out.println("Patient added successfully!");
                }
            }

            case "2" -> userDao.viewPatients();

            case "3" -> {

                int patientId = readId(
                        "Patient ID (0 to cancel): "
                );

                if (patientId <= 0) {
                    System.out.println("Action cancelled.");
                    break;
                }

                System.out.print(
                        "Appointment date (YYYY-MM-DD): "
                );
                String date = scanner.nextLine().trim();

                System.out.print(
                        "Appointment time (HH:MM): "
                );
                String time = scanner.nextLine().trim();

                System.out.print("Reason: ");
                String reason = scanner.nextLine().trim();

                if (date.isEmpty() ||
                        time.isEmpty() ||
                        reason.isEmpty()) {

                    System.out.println("All fields are required.");
                    break;
                }

                if (userDao.addAppointment(
                        patientId, date, time, reason)) {

                    logDao.logAction(
                            currentUser.getId(),
                            "APPOINTMENT_ADDED",
                            "Receptionist "
                                    + currentUser.getId()
                                    + " created appointment for Patient "
                                    + patientId
                    );

                    System.out.println(
                            "Appointment added successfully!"
                    );
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

                int patientId = readId(
                        "Patient ID (0 to cancel): "
                );

                if (patientId <= 0) {
                    System.out.println("Action cancelled.");
                    break;
                }

                System.out.print("Diagnosis: ");
                String diagnosis = scanner.nextLine().trim();

                System.out.print("Treatment: ");
                String treatment = scanner.nextLine().trim();

                if (diagnosis.isEmpty() ||
                        treatment.isEmpty()) {

                    System.out.println(
                            "Diagnosis and treatment are required."
                    );
                    break;
                }

                if (userDao.addPatientRecord(
                        patientId,
                        currentDoctor.getId(),
                        diagnosis,
                        treatment)) {

                    logDao.logAction(
                            currentDoctor.getId(),
                            "RECORD_ADDED",
                            "Doctor " + currentDoctor.getId()
                                    + " added patient record for Patient "
                                    + patientId
                    );

                    System.out.println(
                            "Patient record added successfully!"
                    );
                }
            }

            case "4" -> userDao.viewPatientRecords();

            case "5" -> {

                int appointmentId = readId(
                        "Appointment ID (0 to cancel): "
                );

                if (appointmentId <= 0) {
                    System.out.println("Action cancelled.");
                    break;
                }

                if (userDao.completeAppointment(appointmentId)) {

                    logDao.logAction(
                            currentDoctor.getId(),
                            "APPOINTMENT_COMPLETED",
                            "Doctor " + currentDoctor.getId()
                                    + " completed Appointment "
                                    + appointmentId
                    );

                    System.out.println(
                            "Appointment marked as completed."
                    );

                } else {
                    System.out.println(
                            "Appointment not found or not scheduled."
                    );
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

    private int readId(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {
                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }
}
