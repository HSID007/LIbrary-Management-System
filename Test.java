import java.awt.*;
import javax.swing.*;
//import UserManager;

public class Test {
    static Admin admin = new Admin();
    static Librarian librarian;
    static Student student;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showLoginScreen());
    }

    private static void showLoginScreen() {
        JFrame frame = new JFrame("Library Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 450);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        // Set a gradient background for a more appealing look
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(58, 123, 213); // Blue
            Color color2 = new Color(0, 210, 255);  // Light Blue
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 450, 450);
        frame.setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("Smart Library Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 350, 30);
        frame.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(50, 80, 100, 30);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 80, 220, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 130, 100, 30);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 130, 220, 30);
        frame.add(passField);

        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBounds(50, 180, 100, 30);
        frame.add(roleLabel);

        String[] roles = {"Admin", "Librarian", "Student"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setBounds(150, 180, 220, 30);
        frame.add(roleBox);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(175, 230, 100, 40);
        loginButton.setBackground(new Color(255, 140, 0)); // Dark Orange
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        frame.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(175, 280, 100, 40);
        registerButton.setBackground(new Color(34, 139, 34)); // Forest Green
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        frame.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Save user login info to database (add user if not exists)
                UserManager.saveUserLogin(username, role);

                // Authenticate user
                boolean authenticated = UserManager.authenticateUser(username, password, role);
                if (authenticated) {
                    // Retrieve user ID from database
                    int userId = -1;
                    try (var conn = DBConnection.getConnection()) {
                        String table = null;
                        if ("Student".equalsIgnoreCase(role)) {
                            table = "students";
                        } else if ("Librarian".equalsIgnoreCase(role)) {
                            table = "librarians";
                        } else if ("Admin".equalsIgnoreCase(role)) {
                            table = "admin";
                        }
                        String sql = "SELECT id FROM " + table + " WHERE name = ?";
                        try (var stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, username);
                            var rs = stmt.executeQuery();
                            if (rs.next()) {
                                userId = rs.getInt("id");
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error retrieving user ID: " + ex.getMessage());
                    }

                    // Initialize user objects with ID
                    switch (role) {
                        case "Admin":
                            admin = new Admin();
                            break;
                        case "Librarian":
                            librarian = new Librarian(userId);
                            break;
                        case "Student":
                            student = new Student(userId);
                            break;
                    }

                    frame.dispose();
                    switch (role) {
                        case "Admin":
                            showAdminMenu();
                            break;
                        case "Librarian":
                            showLibrarianMenu();
                            break;
                        case "Student":
                            showStudentMenu();
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter username and password.");
            }
        });

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password to register.");
                return;
            }
            // Register new student
            try {
                UserManager.registerNewStudent(username, password);
                JOptionPane.showMessageDialog(frame, "Registration successful. You can now log in.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Registration failed: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    private static void showAdminMenu() {
        JFrame frame = new JFrame("Admin Panel");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        // Gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(58, 123, 213); // Blue
            Color color2 = new Color(0, 210, 255);  // Light Blue
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80)); // Deep blue-gray
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(7, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

        JButton addLibrarian = new JButton("Add Librarian");
        JButton viewLibrarians = new JButton("View Librarians");
        JButton deleteLibrarian = new JButton("Delete Librarian");
        JButton manageUsers = new JButton("Activate/Deactivate Librarian");
        JButton viewFines = new JButton("View Fine Reports");
        JButton systemReport = new JButton("System Reports");
        JButton logout = new JButton("Logout");

        Color btnBg = new Color(76, 175, 80); // Green
        Color btnFg = Color.WHITE;
        Color borderCol = new Color(33, 150, 243); // Blue

        JButton[] buttons = {addLibrarian, viewLibrarians, deleteLibrarian, manageUsers, viewFines, systemReport, logout};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setBackground(btnBg);
            btn.setForeground(btnFg);
            btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderCol, 2, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
        }

        for (JButton btn : buttons) {
            panel.add(btn);
        }

        backgroundPanel.add(panel, BorderLayout.CENTER);

        addLibrarian.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter librarian name:");
            if (name != null && !name.isEmpty()) {
                String password = JOptionPane.showInputDialog("Enter librarian password:");
                if (password != null && !password.isEmpty()) {
                    admin.addLibrarian(name, password);
                } else {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty.");
                }
            }
        });

        viewLibrarians.addActionListener(e -> admin.viewLibrarians());

        deleteLibrarian.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter librarian name to delete:");
            if (name != null && !name.isEmpty()) admin.deleteLibrarian(name);
        });

        manageUsers.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter librarian name to activate/deactivate:");
            if (name != null && !name.isEmpty()) {
                int option = JOptionPane.showConfirmDialog(null, "Activate this librarian?", "Manage User", JOptionPane.YES_NO_OPTION);
                boolean activate = (option == JOptionPane.YES_OPTION);
                admin.activateDeactivateLibrarian(name, activate);
            }
        });

        viewFines.addActionListener(e -> admin.viewFineReports());

        systemReport.addActionListener(e -> admin.generateSystemReport());

        logout.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        panel.add(addLibrarian);
        panel.add(viewLibrarians);
        panel.add(deleteLibrarian);
        panel.add(manageUsers);
        panel.add(viewFines);
        panel.add(systemReport);
        panel.add(logout);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showLibrarianMenu() {
        JFrame frame = new JFrame("Librarian Panel");
        frame.setSize(500, 550);
        frame.setLocationRelativeTo(null);

        // Gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(63, 81, 181); // Indigo
            Color color2 = new Color(144, 202, 249); // Light Blue
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("Librarian Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80)); // Deep blue-gray
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(10, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

        JButton addBook = new JButton("Add Book");
        JButton viewBooks = new JButton("View Books");
        JButton deleteBook = new JButton("Delete Book");
        JButton issueBook = new JButton("Issue Book");
        JButton viewIssuedBooks = new JButton("View Issued Books");
        JButton returnBook = new JButton("Return Book");
        JButton manageStudents = new JButton("Manage Student Records");
        JButton viewOverdueBooks = new JButton("View Overdue Books");
        JButton notifyStudents = new JButton("Notify Students");
        JButton logout = new JButton("Logout");

        Color btnBg = new Color(76, 175, 80); // Green
        Color btnFg = Color.WHITE;
        Color borderCol = new Color(33, 150, 243); // Blue

        JButton[] buttons = {addBook, viewBooks, deleteBook, issueBook, viewIssuedBooks, returnBook, manageStudents, viewOverdueBooks, notifyStudents, logout};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setBackground(btnBg);
            btn.setForeground(btnFg);
            btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderCol, 2, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
        }

        addBook.addActionListener(e -> {
            String book = JOptionPane.showInputDialog("Enter book name:");
            if (book != null && !book.isEmpty()) librarian.addBook(book);
        });
        viewBooks.addActionListener(e -> librarian.viewBooks());
        deleteBook.addActionListener(e -> {
            String book = JOptionPane.showInputDialog("Enter book name to delete:");
            if (book != null && !book.isEmpty()) librarian.deleteBook(book);
        });
        issueBook.addActionListener(e -> {
            String studentIdStr = JOptionPane.showInputDialog("Enter student ID:");
            String bookTitle = JOptionPane.showInputDialog("Enter book title to issue:");
            if (studentIdStr != null && !studentIdStr.isEmpty() && bookTitle != null && !bookTitle.isEmpty()) {
                try {
                    int studentId = Integer.parseInt(studentIdStr);
                    librarian.issueBook(studentId, bookTitle);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid student ID.");
                }
            }
        });
        viewIssuedBooks.addActionListener(e -> librarian.viewIssuedBooks());
        returnBook.addActionListener(e -> {
            String issueIdStr = JOptionPane.showInputDialog("Enter issue ID to return:");
            if (issueIdStr != null && !issueIdStr.isEmpty()) {
                try {
                    int issueId = Integer.parseInt(issueIdStr);
                    librarian.returnBook(issueId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid issue ID.");
                }
            }
        });
        manageStudents.addActionListener(e -> librarian.manageStudentRecords());
        viewOverdueBooks.addActionListener(e -> librarian.viewOverdueBooks());
        notifyStudents.addActionListener(e -> librarian.notifyStudents());
        logout.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        panel.add(addBook);
        panel.add(viewBooks);
        panel.add(deleteBook);
        panel.add(issueBook);
        panel.add(viewIssuedBooks);
        panel.add(returnBook);
        panel.add(manageStudents);
        panel.add(viewOverdueBooks);
        panel.add(notifyStudents);
        panel.add(logout);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showStudentMenu() {
        JFrame frame = new JFrame("Student Panel");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);

        // Gradient background panel for a modern look
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(70, 130, 180); // Steel Blue
            Color color2 = new Color(0, 191, 165);  // Teal
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("Student Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(9, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

        JButton borrowBook = new JButton("Borrow Book");
        JButton returnBook = new JButton("Return Book");
        JButton viewStatus = new JButton("View Status");
        JButton requestNewBook = new JButton("Request New Book");
        JButton requestHoldBook = new JButton("Request Hold Book");
        JButton reissueBook = new JButton("Reissue Book");
        JButton payFine = new JButton("Pay Fine");
        JButton viewNotifications = new JButton("View Notifications");
        JButton logout = new JButton("Logout");

        Color btnBg = new Color(0, 150, 136); // Teal
        Color btnFg = Color.WHITE;
        Color borderCol = new Color(33, 150, 243); // Blue

        JButton[] buttons = {borrowBook, returnBook, viewStatus, requestNewBook, requestHoldBook, reissueBook, payFine, viewNotifications, logout};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setBackground(btnBg);
            btn.setForeground(btnFg);
            btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderCol, 2, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
        }

        borrowBook.addActionListener(e -> {
            String book = JOptionPane.showInputDialog("Enter book to borrow:");
            if (book != null && !book.isEmpty()) student.borrowBook(librarian, book);
        });
        returnBook.addActionListener(e -> {
            String book = JOptionPane.showInputDialog("Enter book to return:");
            if (book != null && !book.isEmpty()) student.returnBook(librarian, book);
        });
        viewStatus.addActionListener(e -> student.viewStatus());
        requestNewBook.addActionListener(e -> {
            String bookTitle = JOptionPane.showInputDialog("Enter new book title to request:");
            if (bookTitle != null && !bookTitle.isEmpty()) student.requestNewBook(bookTitle);
        });
        requestHoldBook.addActionListener(e -> {
            String bookTitle = JOptionPane.showInputDialog("Enter book title to request hold:");
            if (bookTitle != null && !bookTitle.isEmpty()) student.requestHoldBook(bookTitle);
        });
        reissueBook.addActionListener(e -> {
            String bookTitle = JOptionPane.showInputDialog("Enter book title to reissue:");
            if (bookTitle != null && !bookTitle.isEmpty()) student.reissueBook(bookTitle);
        });
        payFine.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog("Enter fine amount to pay:");
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    student.payFine(amount);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount.");
                }
            }
        });
        viewNotifications.addActionListener(e -> student.viewNotifications());
        logout.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        panel.add(borrowBook);
        panel.add(returnBook);
        panel.add(viewStatus);
        panel.add(requestNewBook);
        panel.add(requestHoldBook);
        panel.add(reissueBook);
        panel.add(payFine);
        panel.add(viewNotifications);
        panel.add(logout);

        frame.add(panel);
        frame.setVisible(true);
    }
}