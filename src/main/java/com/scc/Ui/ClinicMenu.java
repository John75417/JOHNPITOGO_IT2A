
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class ClinicMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public ClinicMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start(User user) throws SQLException {
        boolean running = true;

        while (running) {
            switch (user.getRoles()) {
                case "Administrator":
                    running = showAdministratorMenu();
                    break;

                case "Receptionist":
                    running = showReceptionistMenu();
                    break;

                case "Doctor":
                    running = showDoctorMenu();
                    break;

                default:
                    System.out.println("Unknown role.");
                    running = false;
            }
        }
    }

    private boolean showAdministratorMenu() throws SQLException {
        System.out.println("\n===== ADMINISTRATOR MENU =====");
        System.out.println("1. View User Accounts");
        System.out.println("2. Activate User");
        System.out.println("3. Reject User");
        System.out.println("4. View Patients");
        System.out.println("5. View Appointments");
        System.out.println("6. View Patient Records");
        System.out.println("7. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                userDao.viewUsers();
                break;

            case "2":
                userDao.viewUsers();
                System.out.print("Enter User ID to activate: ");

                try {
                    int id = Integer.parseInt(scanner.nextLine().trim());

                    if (userDao.activateUser(id)) {
                        System.out.println("User activated successfully!");
                    } else {
                        System.out.println(
                                "User not found or account is not PENDING."
                        );
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid numeric ID.");
                }

                System.out.println("\nUpdated User List:");
                userDao.viewUsers();
                break;

            case "3":
                userDao.viewUsers();
                System.out.print("Enter User ID to reject: ");

                try {
                    int id = Integer.parseInt(scanner.nextLine().trim());

                    if (userDao.rejectUser(id)) {
                        System.out.println("User rejected successfully!");
                    } else {
                        System.out.println(
                                "User not found or account is not PENDING."
                        );
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid numeric ID.");
                }

                userDao.viewUsers();
                break;

            case "4":
                userDao.viewPatients();
                break;

            case "5":
                userDao.viewAppointments();
                break;

            case "6":
                userDao.viewPatientRecords();
                break;

            case "7":
                System.out.println("Logged out.");
                return false;

            default:
                System.out.println("Invalid choice.");
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
            case "1":
                System.out.print("Patient name: ");
                String name = scanner.nextLine().trim();

                System.out.print("Patient age: ");
                try {
                    int age = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Patient contact number: ");
                    String contact = scanner.nextLine().trim();

                    userDao.addPatient(name, age, contact);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid age.");
                }
                break;

            case "2":
                userDao.viewPatients();
                break;

            case "3":
                System.out.print("Patient ID: ");
                try {
                    int patientId = Integer.parseInt(
                            scanner.nextLine().trim()
                    );

                    System.out.print("Appointment date (YYYY-MM-DD): ");
                    String date = scanner.nextLine().trim();

                    System.out.print("Appointment time (HH:MM): ");
                    String time = scanner.nextLine().trim();

                    System.out.print("Reason for appointment: ");
                    String reason = scanner.nextLine().trim();

                    userDao.addAppointment(
                            patientId, date, time, reason
                    );
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid patient ID.");
                }
                break;

            case "4":
                userDao.viewAppointments();
                break;

            case "5":
                System.out.println("Logged out.");
                return false;

            default:
                System.out.println("Invalid choice.");
        }

        return true;
    }

    private boolean showDoctorMenu() throws SQLException {
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
            case "1":
                userDao.viewAppointments();
                break;

            case "2":
                userDao.viewPatients();
                break;

            case "3":
                try {
                    System.out.print("Patient ID: ");
                    int patientId = Integer.parseInt(
                            scanner.nextLine().trim()
                    );

                    System.out.print("Diagnosis: ");
                    String diagnosis = scanner.nextLine().trim();

                    System.out.print("Treatment: ");
                    String treatment = scanner.nextLine().trim();

                    userDao.addPatientRecord(
                            patientId, diagnosis, treatment
                    );
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid patient ID.");
                }
                break;

            case "4":
                userDao.viewPatientRecords();
                break;

            case "5":
                try {
                    System.out.print("Appointment ID: ");
                    int appointmentId = Integer.parseInt(
                            scanner.nextLine().trim()
                    );

                    userDao.completeAppointment(appointmentId);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid appointment ID.");
                }
                break;

            case "6":
                System.out.println("Logged out.");
                return false;

            default:
                System.out.println("Invalid choice.");
        }

        return true;
    }
}
