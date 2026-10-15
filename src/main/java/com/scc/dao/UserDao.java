
package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public boolean register(String username, String password, String role) {
        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage() != null
                    && e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Username already exists.");
            } else {
                System.out.println("Registration failed: " + e.getMessage());
            }

            return false;
        }
    }

    public User login(String username, String password) {
        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
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

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return null;
    }
}
