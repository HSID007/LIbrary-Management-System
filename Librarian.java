import java.sql.*;
import javax.swing.*;

public class Librarian {
    private int librarianId;

    public Librarian(int librarianId) {
        this.librarianId = librarianId;
    }

    public void addBook(String book) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO books (title) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, book);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book added to database: " + book);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding book: " + e.getMessage());
        }
    }

    public void viewBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder("Books:\n");
            while (rs.next()) {
                sb.append(rs.getInt("id")).append(": ").append(rs.getString("title")).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching books: " + e.getMessage());
        }
    }

    public void deleteBook(String title) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Book deleted: " + title);
            } else {
                JOptionPane.showMessageDialog(null, "Book not found: " + title);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting book: " + e.getMessage());
        }
    }

    public void issueBook(int studentId, String bookTitle) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if book is available
            String checkSql = "SELECT id FROM books WHERE title = ? AND id NOT IN (SELECT book_id FROM issued_books WHERE returned = FALSE)";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, bookTitle);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("id");
                String issueSql = "INSERT INTO issued_books (book_id, student_id, issue_date, returned) VALUES (?, ?, CURRENT_DATE, FALSE)";
                PreparedStatement issueStmt = conn.prepareStatement(issueSql);
                issueStmt.setInt(1, bookId);
                issueStmt.setInt(2, studentId);
                issueStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book issued to student ID: " + studentId);
            } else {
                JOptionPane.showMessageDialog(null, "Book not available for issue.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error issuing book: " + e.getMessage());
        }
    }

    public void viewIssuedBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ib.id, b.title, ib.student_id, ib.issue_date, ib.returned FROM issued_books ib JOIN books b ON ib.book_id = b.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder("Issued Books:\n");
            while (rs.next()) {
                sb.append("Issue ID: ").append(rs.getInt("id"))
                  .append(", Book: ").append(rs.getString("title"))
                  .append(", Student ID: ").append(rs.getInt("student_id"))
                  .append(", Issue Date: ").append(rs.getDate("issue_date"))
                  .append(", Returned: ").append(rs.getBoolean("returned") ? "Yes" : "No")
                  .append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching issued books: " + e.getMessage());
        }
    }

    public void returnBook(int issueId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE issued_books SET returned = TRUE, return_date = CURRENT_DATE WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, issueId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Calculate fine if any
                String fineSql = "SELECT DATEDIFF(CURRENT_DATE, issue_date) - 14 AS overdue_days FROM issued_books WHERE id = ?";
                PreparedStatement fineStmt = conn.prepareStatement(fineSql);
                fineStmt.setInt(1, issueId);
                ResultSet rs = fineStmt.executeQuery();
                if (rs.next()) {
                    int overdueDays = rs.getInt("overdue_days");
                    double fine = 0;
                    if (overdueDays > 0) {
                        fine = overdueDays * 5; // 5 currency units per day
                    }
                    String updateFineSql = "UPDATE issued_books SET fine = ? WHERE id = ?";
                    PreparedStatement updateFineStmt = conn.prepareStatement(updateFineSql);
                    updateFineStmt.setDouble(1, fine);
                    updateFineStmt.setInt(2, issueId);
                    updateFineStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Book returned. Fine: ₹" + fine);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Issue record not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error returning book: " + e.getMessage());
        }
    }

    public void manageStudentRecords() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name FROM students";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder("Student Records:\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append(", Name: ").append(rs.getString("name"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching student records: " + e.getMessage());
        }
    }

    public void requestApproval(String request) {
        // Placeholder for request approval logic
        JOptionPane.showMessageDialog(null, "Request submitted for approval: " + request);
    }

    public void viewOverdueBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ib.id, b.title, ib.student_id, DATEDIFF(CURRENT_DATE, ib.issue_date) AS days_issued " +
                         "FROM issued_books ib JOIN books b ON ib.book_id = b.id " +
                         "WHERE ib.returned = FALSE AND DATEDIFF(CURRENT_DATE, ib.issue_date) > 14";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder("Overdue Books:\n");
            while (rs.next()) {
                sb.append("Issue ID: ").append(rs.getInt("id"))
                  .append(", Book: ").append(rs.getString("title"))
                  .append(", Student ID: ").append(rs.getInt("student_id"))
                  .append(", Days Issued: ").append(rs.getInt("days_issued"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching overdue books: " + e.getMessage());
        }
    }

     public void notifyStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            // Get all students with overdue books
            String sql = "SELECT DISTINCT student_id FROM issued_books WHERE returned = FALSE AND DATEDIFF(CURRENT_DATE, issue_date) > 14";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                // Insert notification message into a notifications table or update a notifications field
                // For simplicity, let's assume a notifications table with student_id and message columns
                String message = "You have overdue books. Please return them as soon as possible.";
                String insertSql = "INSERT INTO notifications (student_id, message) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, studentId);
                    insertStmt.setString(2, message);
                    insertStmt.executeUpdate();
                } catch (SQLException e) {
                    // Ignore duplicate or other errors for simplicity
                }
            }
            JOptionPane.showMessageDialog(null, "Notifications sent to students with overdue books.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error sending notifications: " + e.getMessage());
        }
    }
}
