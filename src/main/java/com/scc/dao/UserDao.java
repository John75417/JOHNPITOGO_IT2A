package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserDao {

    // ==================== USER OPERATIONS ====================

    public boolean register(String username, String password, String role)
            throws SQLException {

        String sql = """
            INSERT INTO users (username, password, roles, status)
            VALUES (?, ?, ?, 'PENDING')
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.toUpperCase());

            return ps.executeUpdate() > 0;
        }
    }

    public User login(String username, String password)
            throws SQLException {

        String sql = """
            SELECT id, username, password, roles, status
            FROM users
            WHERE username = ? AND password = ?
            AND UPPER(status) = 'ACTIVE'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }
        }

        return null;
    }

    public void viewUsers() throws SQLException {
        String sql = """
            SELECT id, username, roles, status
            FROM users
            ORDER BY id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==================================================");
            System.out.printf("%-8s %-20s %-18s %-12s%n",
                    "ID", "USERNAME", "ROLE", "STATUS");
            System.out.println("==================================================");

            while (rs.next()) {
                System.out.printf("%-8d %-20s %-18s %-12s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status"));
            }

            System.out.println("==================================================");
        }
    }

    public User getUserById(int userId) throws SQLException {
        String sql = """
            SELECT id, username, password, roles, status
            FROM users
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }
        }

        return null;
    }

    // Required administrative update method.
    public boolean updateUserRoleAndStatus(
            int userId, String newRole, String newStatus)
            throws SQLException {

        String sql = """
            UPDATE users
            SET roles = ?, status = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newRole.toUpperCase());
            ps.setString(2, newStatus.toUpperCase());
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean activateUser(int userId) throws SQLException {
        String sql = """
            UPDATE users
            SET status = 'ACTIVE'
            WHERE id = ? AND UPPER(status) = 'PENDING'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rejectUser(int userId) throws SQLException {
        String sql = """
            UPDATE users
            SET status = 'REJECTED'
            WHERE id = ? AND UPPER(status) = 'PENDING'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ==================== PATIENT OPERATIONS ====================

    public boolean addPatient(String name, int age, String contact)
            throws SQLException {

        String sql = """
            INSERT INTO patients
            (full_name, age, gender, contact, address)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, "Not specified");
            ps.setString(4, contact);
            ps.setString(5, "Not specified");

            return ps.executeUpdate() > 0;
        }
    }

    public boolean addPatient(String name, int age, String gender,
                              String contact, String address)
            throws SQLException {

        String sql = """
            INSERT INTO patients
            (full_name, age, gender, contact, address)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, contact);
            ps.setString(5, address);

            return ps.executeUpdate() > 0;
        }
    }

    public void viewPatients() throws SQLException {
        String sql = """
            SELECT id, full_name, age, gender, contact, address
            FROM patients
            ORDER BY id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== PATIENT LIST =====");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id")
                                + " | Name: " + rs.getString("full_name")
                                + " | Age: " + rs.getInt("age")
                                + " | Gender: " + rs.getString("gender")
                                + " | Contact: " + rs.getString("contact")
                                + " | Address: " + rs.getString("address")
                );
            }
        }
    }

    // ==================== APPOINTMENT OPERATIONS ====================

    public boolean addAppointment(int patientId, String date,
                                  String time, String reason)
            throws SQLException {

        String sql = """
            INSERT INTO appointments
            (patient_id, appointment_date, appointment_time, reason)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, reason);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean addAppointment(int patientId, int doctorId,
                                  String date, String time, String reason)
            throws SQLException {

        String sql = """
            INSERT INTO appointments
            (patient_id, doctor_id, appointment_date,
             appointment_time, reason)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, reason);

            return ps.executeUpdate() > 0;
        }
    }

    public void viewAppointments() throws SQLException {
        String sql = """
            SELECT a.id, a.patient_id, p.full_name,
                   a.doctor_id, a.appointment_date,
                   a.appointment_time, a.reason, a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.id
            ORDER BY a.id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== APPOINTMENT LIST =====");

            while (rs.next()) {
                System.out.println(
                        "Appointment ID: " + rs.getInt("id")
                                + " | Patient: " + rs.getString("full_name")
                                + " | Date: " + rs.getString("appointment_date")
                                + " | Time: " + rs.getString("appointment_time")
                                + " | Reason: " + rs.getString("reason")
                                + " | Status: " + rs.getString("status")
                );
            }
        }
    }

    public boolean completeAppointment(int appointmentId)
            throws SQLException {

        String sql = """
            UPDATE appointments
            SET status = 'COMPLETED'
            WHERE id = ? AND status = 'SCHEDULED'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ==================== PATIENT RECORD OPERATIONS ====================

    public boolean addPatientRecord(int patientId, int doctorId,
                                    String diagnosis, String treatment)
            throws SQLException {

        String sql = """
            INSERT INTO patient_records
            (patient_id, doctor_id, diagnosis, treatment)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean addPatientRecord(int patientId, int doctorId,
                                    int appointmentId,
                                    String diagnosis, String treatment,
                                    String notes)
            throws SQLException {

        String sql = """
            INSERT INTO patient_records
            (patient_id, doctor_id, appointment_id,
             diagnosis, treatment, notes)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);

            if (appointmentId <= 0) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, appointmentId);
            }

            ps.setString(4, diagnosis);
            ps.setString(5, treatment);
            ps.setString(6, notes);

            return ps.executeUpdate() > 0;
        }
    }

    public void viewPatientRecords() throws SQLException {
        String sql = """
            SELECT r.id, r.patient_id, p.full_name,
                   r.doctor_id, r.appointment_id,
                   r.diagnosis, r.treatment, r.notes, r.created_at
            FROM patient_records r
            JOIN patients p ON r.patient_id = p.id
            ORDER BY r.id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== PATIENT RECORDS =====");

            while (rs.next()) {
                System.out.println(
                        "Record ID: " + rs.getInt("id")
                                + " | Patient: " + rs.getString("full_name")
                                + " | Doctor ID: " + rs.getInt("doctor_id")
                                + " | Appointment ID: " + rs.getString("appointment_id")
                                + " | Diagnosis: " + rs.getString("diagnosis")
                                + " | Treatment: " + rs.getString("treatment")
                                + " | Notes: " + rs.getString("notes")
                                + " | Created: " + rs.getString("created_at")
                );
            }
        }
    }
}