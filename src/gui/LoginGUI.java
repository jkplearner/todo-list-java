package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JPasswordField confirmPasswordField;
    private JPanel cardPanel;
    private Connection connection;

    public LoginGUI() {
        super("Login or Signup");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        establishDatabaseConnection();
        initializeCardLayout();
        askLoginOrSignup();
    }

    private void initializeCardLayout() {
        cardPanel = new JPanel(new CardLayout());
        add(cardPanel);
    }
    private void establishDatabaseConnection() {
        try {
            // Connect to MySQL using MySQL Connector/J
            String jdbcUrl = "";
            String username = "root"; // Replace with your MySQL username
            String password = ""; // Replace with your MySQL password
    
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            createUsersTable();  // Create the users table if it doesn't exist
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database. Please check your database connection.");
            System.exit(1);
        }
    }
    
    
    
    private void createUsersTable() {
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255), name VARCHAR(255))")) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void askLoginOrSignup() {
        JPanel menuPanel = new JPanel(new GridLayout(2, 1));

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        loginButton.addActionListener(e -> showLoginForm());
        signupButton.addActionListener(e -> showSignupForm());

        menuPanel.add(loginButton);
        menuPanel.add(signupButton);

        cardPanel.add(menuPanel, "Menu");
        showCard("Menu");

        setVisible(true);
    }

    private void showLoginForm() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));


        // Set the title to "TaskHUB Login form"
        setTitle(loginPanel, "TaskHUB Login form");
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(usernameField, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> login());

        loginPanel.add(centerPanel, BorderLayout.CENTER);
        cardPanel.add(loginPanel, "Login");
        showCard("Login");
    }

    private void showSignupForm() {
        JPanel signupPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        nameField = new JTextField(20);
        nameField.setFont(new Font("Dialog", Font.PLAIN, 18));

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));

        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        confirmPasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Dialog", Font.PLAIN, 18));


        JButton signupButton = new JButton("Signup");
        signupButton.setFont(new Font("Dialog", Font.BOLD, 18));
        setTitle(signupPanel, "TaskHUB Signup form");
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(nameLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(nameField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(usernameLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(usernameField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(passwordField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(confirmPasswordLabel, gbc);
    
        gbc.gridx = 1;
        gbc.gridy = 3;
        centerPanel.add(confirmPasswordField, gbc);
    
    
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(signupButton, gbc);

        signupButton.addActionListener(e -> signup());

        signupPanel.add(centerPanel, BorderLayout.CENTER);
        cardPanel.add(signupPanel, "Signup");
        showCard("Signup");
    }
    private void setTitle(JPanel panel, String title) {
        // Add a title label with white text on a black background
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
    }

    private void openTodoList(String username) {
        dispose();
        SwingUtilities.invokeLater(() -> new TodoListGUI(username));
    }

    private void login() {
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        if (checkCredentials(enteredUsername, enteredPassword)) {
            JOptionPane.showMessageDialog(this, "LOGIN SUCCESSFUL!");
            openTodoList(enteredUsername);
        } else {
            JOptionPane.showMessageDialog(this, "INCORRECT USERNAME OR PASSWORD");
        }
    }

    private boolean checkCredentials(String enteredUsername, String enteredPassword) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, enteredUsername);
            preparedStatement.setString(2, enteredPassword);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void signup() {
        String enteredName = nameField.getText();
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());
        if (checkUsernameExists(enteredUsername)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Signup failed.");
            return;
        }

        if (!checkPasswordMatch()) {
            JOptionPane.showMessageDialog(this, "Password and confirm password do not match. Signup failed.");
            return;
        }

        if (saveCredentials(enteredName, enteredUsername, enteredPassword)) {
            JOptionPane.showMessageDialog(this, "SIGNUP SUCCESSFUL!");
        } else {
            JOptionPane.showMessageDialog(this, "SIGNUP FAILED...");
        }

        showCard("Menu");
    }

    private boolean checkUsernameExists(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkPasswordMatch() {
        String enteredPassword = new String(passwordField.getPassword());
        String confirmedPassword = new String(confirmPasswordField.getPassword());

        return enteredPassword.equals(confirmedPassword);
    }

    private boolean saveCredentials(String name, String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showCard(String cardName) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, cardName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}