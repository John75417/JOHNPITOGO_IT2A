
package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserDao {

    // USER OPERATIONS

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
            ps.setString(3, role);

            return ps.executeUpdate() > 0;
        }
    }

    public User login(String username, String password)
            throws SQLException {

        String sql = """
            SELECT id, username, password, roles, status
            FROM users
            WHERE username = ? AND password = ? AND status = 'ACTIVE'
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

            System.out.println("\n========== REGISTERED USERS ==========");

            System.out.printf(
                    "%-5s %-20s %-18s %-12s%n",
                    "ID", "USERNAME", "ROLE", "STATUS"
            );

            System.out.println("-----------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.printf(
                        "%-5d %-20s %-18s %-12s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No registered users found.");
            }
        }
    }


    public boolean activateUser(int id) throws SQLException {
        String sql = """
            UPDATE users SET status = 'ACTIVE'
            WHERE id = ? AND status = 'PENDING'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rejectUser(int id) throws SQLException {
        String sql = """
            UPDATE users SET status = 'REJECTED'
            WHERE id = ? AND status = 'PENDING'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // PATIENT OPERATIONS

    public boolean addPatient(String name, int age, String gender,
                              String contact, String address)
            throws SQLException {

        String sql = """
            INSERT INTO patients (full_name, age, gender, contact, address)
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

    public void addPatient(String name, int age, String contact)
            throws SQLException {

        String sql = """
        INSERT INTO patients (name, age, contact)
        VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, contact);

            ps.executeUpdate();
            System.out.println("Patient added successfully!");
        }
    }

    public void addAppointment(
            int patientId, String date, String time, String reason)
            throws SQLException {

        String sql = """
        INSERT INTO appointments
        (patient_id, appointment_date, appointment_time, reason, status)
        VALUES (?, ?, ?, ?, 'Scheduled')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, reason);

            ps.executeUpdate();
            System.out.println("Appointment added successfully!");
        }
    }

    public void addPatientRecord(
            int patientId, String diagnosis, String treatment)
            throws SQLException {

        String sql = """
        INSERT INTO patient_records
        (patient_id, diagnosis, treatment)
        VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setString(2, diagnosis);
            ps.setString(3, treatment);

            ps.executeUpdate();
            System.out.println("Patient record added successfully!");
        }
    }


    public void viewPatients() throws SQLException {
        String sql = "SELECT * FROM patients ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== PATIENTS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf(
                        "ID: %d | Name: %s | Age: %d | Gender: %s | Contact: %s | Address: %s%n",
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("address")
                );
            }

            if (!found) {
                System.out.println("No patients found.");
            }
        }
    }

    // APPOINTMENT OPERATIONS

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

            if (doctorId > 0) {
                ps.setInt(2, doctorId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, reason);

            return ps.executeUpdate() > 0;
        }
    }

    public void viewAppointments() throws SQLException {
        String sql = """
            SELECT a.id, p.full_name, u.username AS doctor,
                   a.appointment_date, a.appointment_time,
                   a.reason, a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.id
            LEFT JOIN users u ON a.doctor_id = u.id
            ORDER BY a.appointment_date, a.appointment_time
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== APPOINTMENTS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf(
                        "ID: %d | Patient: %s | Doctor: %s | Date: %s | Time: %s | Reason: %s | Status: %s%n",
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("doctor"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("reason"),
                        rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No appointments found.");
            }
        }
    }

    public boolean completeAppointment(int appointmentId)
            throws SQLException {

        String sql = """
            UPDATE appointments SET status = 'COMPLETED'
            WHERE id = ? AND status = 'SCHEDULED'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // PATIENT RECORD OPERATIONS

    public boolean addPatientRecord(int patientId, int doctorId,
                                    int appointmentId, String diagnosis,
                                    String treatment, String notes)
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

            if (appointmentId > 0) {
                ps.setInt(3, appointmentId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setString(4, diagnosis);
            ps.setString(5, treatment);
            ps.setString(6, notes);

            return ps.executeUpdate() > 0;
        }
    }

    public void viewPatientRecords() throws SQLException {
        String sql = """
            SELECT r.id, p.full_name, u.username AS doctor,
                   r.diagnosis, r.treatment, r.notes, r.created_at
            FROM patient_records r
            JOIN patients p ON r.patient_id = p.id
            JOIN users u ON r.doctor_id = u.id
            ORDER BY r.id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== PATIENT RECORDS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf(
                        "Record ID: %d | Patient: %s | Doctor: %s | Diagnosis: %s | Treatment: %s | Notes: %s | Created: %s%n",
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("doctor"),
                        rs.getString("diagnosis"),
                        rs.getString("treatment"),
                        rs.getString("notes"),
                        rs.getString("created_at")
                );
            }

            if (!found) {
                System.out.println("No patient records found.");
            }
        }
    }
}
