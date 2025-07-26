import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class Main implements ActionListener {

    JFrame frame;
    JButton button;

    public static void main(String[] args) {
        Main mainApp = new Main();
        mainApp.createUI();
    }

    public void createUI() {
        frame = new JFrame();
        frame.setTitle("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(800, 600); // Wider and shorter for better fit
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(34, 40, 49));

        // Title with shadow
        JLabel shadowText = new JLabel("Welcome to our Library Management System");
        shadowText.setBounds(53, 33, 700, 60);
        shadowText.setFont(new Font("Segoe UI", Font.BOLD, 32));
        shadowText.setForeground(new Color(0,0,0,80));
        shadowText.setHorizontalAlignment(JLabel.CENTER);
        frame.add(shadowText);

        JLabel textLabel = new JLabel("Welcome to our Library Management System");
        textLabel.setBounds(50, 30, 700, 60);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        textLabel.setForeground(new Color(238, 238, 238));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(textLabel);

        // Subtitle
        JLabel subtitle = new JLabel("Manage books, users, and more with ease.");
        subtitle.setBounds(200, 90, 400, 35);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(new Color(0, 173, 181));
        subtitle.setHorizontalAlignment(JLabel.CENTER);
        frame.add(subtitle);

        // Logo with drop shadow effect
        int imgWidth = 180;
        int imgHeight = 180;
        int imgX = (frame.getWidth() - imgWidth) / 2;
        int imgY = 150;

        ImageIcon originalIcon = new ImageIcon("Logo.png");
        ImageIcon scaledIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(imgWidth, imgHeight, java.awt.Image.SCALE_SMOOTH));

        JLabel shadowLabel = new JLabel();
        shadowLabel.setBounds(imgX + 6, imgY + 6, imgWidth, imgHeight);
        shadowLabel.setOpaque(false);
        shadowLabel.setIcon(new ImageIcon(originalIcon.getImage().getScaledInstance(imgWidth, imgHeight, java.awt.Image.SCALE_SMOOTH)));
        shadowLabel.setForeground(new Color(0,0,0,80));
        shadowLabel.setVisible(false); // Shadow effect not visible with icon, so hide
        frame.add(shadowLabel);

        Border border = BorderFactory.createLineBorder(new Color(0, 173, 181), 4, true);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBounds(imgX, imgY, imgWidth, imgHeight);
        imageLabel.setBorder(border);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(imageLabel);

        // Stylish Login Button
        button = new JButton("Login");
        button.setBounds((frame.getWidth() - 160) / 2, 370, 160, 50);
        button.addActionListener(this);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setBackground(new Color(0, 173, 181));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(238, 238, 238), 2, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        frame.add(button);

        // Footer
        JLabel footer = new JLabel("© 2025 Library Management Team");
        footer.setBounds(0, 540, 800, 30);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.setForeground(new Color(120, 144, 156));
        footer.setHorizontalAlignment(JLabel.CENTER);
        frame.add(footer);

        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            frame.dispose();
            Test.main(null);
        }
    }
    
}
