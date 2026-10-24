
package com.scc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                createUsersTableIfMissing(conn);
                migrateOldUserRoles(conn);
                createClinicTables(conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        System.out.println("Database initialized successfully.");
    }

    private static void createUsersTableIfMissing(Connection conn)
            throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL CHECK (
                    roles IN ('Administrator', 'Receptionist', 'Doctor')
                ),
                status TEXT NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED'))
            )
            """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    private static void migrateOldUserRoles(Connection conn)
            throws SQLException {

        String tableSql = "";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT sql FROM sqlite_master " +
                             "WHERE type = 'table' AND name = 'users'")) {

            if (rs.next()) {
                tableSql = rs.getString("sql");
            }
        }

        if (tableSql == null || tableSql.isEmpty()) {
            return;
        }

        boolean oldRoles =
                tableSql.contains("TEACHER") ||
                        tableSql.contains("STUDENT");

        if (!oldRoles) {
            return;
        }

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE users_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL CHECK (
                        roles IN ('Administrator', 'Receptionist', 'Doctor')
                    ),
                    status TEXT NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('ACTIVE', 'PENDING', 'REJECTED'))
                )
                """);

            st.executeUpdate("""
                INSERT INTO users_new (id, username, password, roles, status)
                SELECT id, username, password,
                    CASE
                        WHEN UPPER(roles) = 'TEACHER' THEN 'Doctor'
                        WHEN UPPER(roles) = 'STUDENT' THEN 'Receptionist'
                        WHEN LOWER(roles) = 'administrator'
                            THEN 'Administrator'
                        WHEN LOWER(roles) = 'doctor' THEN 'Doctor'
                        WHEN LOWER(roles) = 'receptionist'
                            THEN 'Receptionist'
                        ELSE 'Receptionist'
                    END,
                    CASE
                        WHEN UPPER(status) IN ('ACTIVE', 'PENDING', 'REJECTED')
                            THEN UPPER(status)
                        ELSE 'PENDING'
                    END
                FROM users
                """);

            st.executeUpdate("DROP TABLE users");
            st.executeUpdate("ALTER TABLE users_new RENAME TO users");
        }
    }

    private static void createClinicTables(Connection conn)
            throws SQLException {

        String[] sqlStatements = {
                """
            CREATE TABLE IF NOT EXISTS patients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                age INTEGER NOT NULL,
                gender TEXT NOT NULL,
                contact TEXT NOT NULL,
                address TEXT NOT NULL
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id INTEGER NOT NULL,
                doctor_id INTEGER,
                appointment_date TEXT NOT NULL,
                appointment_time TEXT NOT NULL,
                reason TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'SCHEDULED',
                FOREIGN KEY (patient_id) REFERENCES patients(id),
                FOREIGN KEY (doctor_id) REFERENCES users(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS patient_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id INTEGER NOT NULL,
                doctor_id INTEGER NOT NULL,
                appointment_id INTEGER,
                diagnosis TEXT NOT NULL,
                treatment TEXT NOT NULL,
                notes TEXT,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (patient_id) REFERENCES patients(id),
                FOREIGN KEY (doctor_id) REFERENCES users(id),
                FOREIGN KEY (appointment_id) REFERENCES appointments(id)
            )
            """
        };

        try (Statement st = conn.createStatement()) {
            for (String sql : sqlStatements) {
                st.executeUpdate(sql);
            }
        }
    }
}
