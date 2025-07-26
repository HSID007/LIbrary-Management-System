import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class Student {
    private int studentId;
    ArrayList<String> myBooks = new ArrayList<>();
    ArrayList<String> notifications = new ArrayList<>();

    public Student(int studentId) {
        this.studentId = studentId;
    }

    public void borrowBook(Librarian librarian, String book) {
        try {
            librarian.issueBook(studentId, book);
            myBooks.add(book);
            notifications.add("Borrowed: " + book);
            JOptionPane.showMessageDialog(null, "Borrowed: " + book);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error borrowing book: " + e.getMessage());
        }
    }

    public void returnBook(Librarian librarian, String book) {
        if (myBooks.remove(book)) {
            notifications.add("Returned: " + book);
            JOptionPane.showMessageDialog(null, "Returned: " + book);
        } else {
            JOptionPane.showMessageDialog(null, "You haven't borrowed this book.");
        }
    }

    public void viewStatus() {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT b.title FROM issued_books ib JOIN books b ON ib.book_id = b.id WHERE ib.student_id = ? AND ib.returned = FALSE";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();
        StringBuilder sb = new StringBuilder("Your Borrowed Books:\n");
        while (rs.next()) {
            sb.append(rs.getString("title")).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching borrowed books: " + e.getMessage());
    }
}

    public void viewNotifications() {
        JOptionPane.showMessageDialog(null, String.join("\n", notifications));
    }

    public void requestNewBook(String bookTitle) {
        // Placeholder for requesting new book logic
        JOptionPane.showMessageDialog(null, "Request for new book submitted: " + bookTitle);
    }

    public void requestHoldBook(String bookTitle) {
        // Placeholder for requesting hold book logic
        JOptionPane.showMessageDialog(null, "Request for hold book submitted for 1 week: " + bookTitle);
    }

    public void reissueBook(String bookTitle) {
        // Placeholder for reissue book logic
        JOptionPane.showMessageDialog(null, "Reissue request submitted for book: " + bookTitle);
    }

    public void payFine(double amount) {
        // Placeholder for paying fine logic
        JOptionPane.showMessageDialog(null, "Fine paid: ₹" + amount);
    }
}