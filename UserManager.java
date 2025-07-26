import java.sql.*;

public class UserManager {

    public static void saveUserLogin(String username, String role) {
        try (Connection conn = DBConnection.getConnection()) {
            if ("Student".equalsIgnoreCase(role)) {
                saveStudent(conn, username);
            } else if ("Librarian".equalsIgnoreCase(role)) {
                saveLibrarian(conn, username);
            } else if ("Admin".equalsIgnoreCase(role)) {
                saveAdmin(conn, username);
            }
        } catch (SQLException e) {
            System.err.println("Error saving user login: " + e.getMessage());
        }
    }

    public static boolean authenticateUser(String username, String password, String role) {
        try (Connection conn = DBConnection.getConnection()) {
            String table = null;
            if ("Student".equalsIgnoreCase(role)) {
                table = "students";
            } else if ("Librarian".equalsIgnoreCase(role)) {
                table = "librarians";
            } else if ("Admin".equalsIgnoreCase(role)) {
                table = "admin";
            } else {
                return false;
            }

            String sql = "SELECT password FROM " + table + " WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    private static void saveStudent(Connection conn, String username) throws SQLException {
        // Check if student exists
        String checkSql = "SELECT id FROM students WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Insert new student with default password
                String insertSql = "INSERT INTO students (name, password, active) VALUES (?, 'default123', 1)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private static void saveLibrarian(Connection conn, String username) throws SQLException {
        // Check if librarian exists
        String checkSql = "SELECT id FROM librarians WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Insert new librarian with default password
                String insertSql = "INSERT INTO librarians (name, password, active) VALUES (?, 'default123', 1)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private static void saveAdmin(Connection conn, String username) throws SQLException {
        // Save admin to admin table
        String checkSql = "SELECT id FROM admin WHERE name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Insert new admin with default password
                String insertSql = "INSERT INTO admin (name, password, active) VALUES (?, 'default123', 1)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public static void registerNewStudent(String username, String password) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM students WHERE name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    throw new SQLException("Username already exists.");
                }
            }
            String insertSql = "INSERT INTO students (name, password, active) VALUES (?, ?, 1)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
            }
        }
    }
}