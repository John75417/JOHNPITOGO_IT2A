
package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Disable foreign keys temporarily
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = OFF");
            }

            conn.setAutoCommit(false);

            try {

                createUsersTable(conn);
                createClinicTables(conn);

                conn.commit();

                System.out.println("Database initialized successfully.");

            } catch (SQLException e) {

                conn.rollback();
                throw e;

            } finally {

                conn.setAutoCommit(true);

                try (Statement st = conn.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
        }
    }

    // ==========================================
    // USERS TABLE
    // ==========================================

    private static void createUsersTable(Connection conn)
            throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,

                username TEXT NOT NULL UNIQUE,

                password TEXT NOT NULL,

                roles TEXT NOT NULL
                    CHECK (
                        roles IN (
                            'Administrator',
                            'Receptionist',
                            'Doctor'
                        )
                    ),

                status TEXT NOT NULL DEFAULT 'PENDING'
            )
            """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    // ==========================================
    // PATIENTS TABLE
    // ==========================================

    private static void createPatientsTable(Connection conn)
            throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS patients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,

                full_name TEXT NOT NULL,

                age INTEGER NOT NULL,

                gender TEXT NOT NULL,

                contact TEXT NOT NULL,

                address TEXT NOT NULL
            )
            """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    // ==========================================
    // APPOINTMENTS TABLE
    // ==========================================

    private static void createAppointmentsTable(Connection conn)
            throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,

                patient_id INTEGER NOT NULL,

                doctor_id INTEGER,

                appointment_date TEXT NOT NULL,

                appointment_time TEXT NOT NULL,

                reason TEXT NOT NULL,

                status TEXT NOT NULL DEFAULT 'SCHEDULED',

                FOREIGN KEY (patient_id)
                    REFERENCES patients(id),

                FOREIGN KEY (doctor_id)
                    REFERENCES users(id)
            )
            """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    // ==========================================
    // PATIENT RECORDS TABLE
    // ==========================================

    private static void createPatientRecordsTable(Connection conn)
            throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS patient_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,

                patient_id INTEGER NOT NULL,

                doctor_id INTEGER NOT NULL,

                appointment_id INTEGER,

                diagnosis TEXT NOT NULL,

                treatment TEXT NOT NULL,

                notes TEXT,

                created_at TEXT DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (patient_id)
                    REFERENCES patients(id),

                FOREIGN KEY (doctor_id)
                    REFERENCES users(id),

                FOREIGN KEY (appointment_id)
                    REFERENCES appointments(id)
            )
            """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    // ==========================================
    // CREATE ALL CLINIC TABLES
    // ==========================================

    private static void createClinicTables(Connection conn)
            throws SQLException {

        createPatientsTable(conn);

        createAppointmentsTable(conn);

        createPatientRecordsTable(conn);
    }
}
