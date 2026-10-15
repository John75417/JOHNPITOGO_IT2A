
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:clinic.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "SQLite JDBC driver not found. Reload Maven and check pom.xml.",
                    e
            );
        }

        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL
                    CHECK (roles IN ('TEACHER', 'STUDENT')),
                status TEXT NOT NULL DEFAULT 'PENDING'
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("SQLite database connected successfully!");
            System.out.println("Users table created successfully!");
        }
    }
}
