package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.sql.*;
import java.util.Random;


public class TodoListGUI extends JFrame {
    private DefaultListModel<String> todoListModel;
    private JList<String> todoList;
    private JTextField taskField;
    private JComboBox<String> hoursComboBox;
    private JComboBox<String> minutesComboBox;
    private Timer notificationTimer;
    private JButton notesButton;
    private JTextArea notesTextArea;
    private String username;
    private JButton hiddenNotesButton;
    private JTextArea hiddenNotesTextArea;
    private String hiddenNotesPassword;
    private Connection connection;
    private JLabel quoteLabel;
    private String[] quotes = {
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
            "The only limit to our realization of tomorrow will be our doubts of today. - Franklin D. Roosevelt"
    };

    public TodoListGUI(String username) {
        super("Todo List");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 400);
        setLocationRelativeTo(null);
        this.username = username;
        todoListModel = new DefaultListModel<>();
        // JDBC URL, username, and password of SQL Server
        String jdbcUrl = "";
        String dbUsername = "root"; // Replace with your MySQL username
        String dbPassword = ""; // Replace with your MySQL password

    try {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Connect to MySQL using the specified username and password
        connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        // Create tables if they don't exist
        createTables();

        // Load data from the database
        loadTasks(username);
        loadHiddenNotesPassword(username);

        // Other initialization steps, if any
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to connect to the database. Please check your database connection.");
        System.exit(1);
    }

    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    });

        

    todoList = new JList<>(todoListModel);
    todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    todoList.setCellRenderer(new CompletedTaskRenderer());

        JScrollPane scrollPane = new JScrollPane(todoList);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(1, 5));
        taskField = new JTextField();
        inputPanel.add(taskField);

        hoursComboBox = new JComboBox<>(getHours());
        inputPanel.add(hoursComboBox);

        minutesComboBox = new JComboBox<>(getMinutes());
        inputPanel.add(minutesComboBox);

// Add the new code here
        JLabel hoursLabel = new JLabel("Hours: ");
        JLabel minutesLabel = new JLabel("Minutes: ");

        inputPanel.add(hoursLabel);
        inputPanel.add(hoursComboBox);
        inputPanel.add(minutesLabel);
        inputPanel.add(minutesComboBox);


        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        inputPanel.add(addButton);

        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeTask();
            }
        });
        inputPanel.add(completeButton);

        JButton removeButton = new JButton("Remove Task");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTask();
            }
        });
        inputPanel.add(removeButton);
        notesButton = new JButton("Notes"); // Create the notes button
        notesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNotes(username); // Pass the username to openNotes
            }
        });
        inputPanel.add(notesButton);
        quoteLabel = new JLabel(getRandomQuote());
        mainPanel.add(quoteLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        notesTextArea = new JTextArea();
        loadTasks(username); // Load tasks specific to the user
        loadNotes(username);
        loadTasks(username);  // Load tasks specific to the user
        loadHiddenNotesPassword(username);
        // Initialize the timer to check for notifications every minute
        notificationTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkNotifications();
            }
        });
        notificationTimer.start();
        hiddenNotesButton = new JButton("Hidden Notes");
        hiddenNotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHiddenNotes(username);
            }
        });
        inputPanel.add(hiddenNotesButton);

        setVisible(true);
    }
    private String getRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        return quotes[index];
    }
    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create Tasks table
            statement.execute("CREATE TABLE IF NOT EXISTS tasks (username TEXT, task TEXT)");

            // Create Notes table
            statement.execute("CREATE TABLE IF NOT EXISTS notes (username TEXT, note TEXT)");

            // Create HiddenNotes table
            statement.execute("CREATE TABLE IF NOT EXISTS hidden_notes (username TEXT, hidden_note TEXT)");

            // Create HiddenNotesPassword table
            statement.execute("CREATE TABLE IF NOT EXISTS hidden_notes_password (username TEXT, password TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void openHiddenNotes(String username) {
        JFrame hiddenNotesFrame = new JFrame("Hidden Notes");
        hiddenNotesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hiddenNotesFrame.setSize(400, 300);
        hiddenNotesFrame.setLocationRelativeTo(null);

        hiddenNotesTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(hiddenNotesTextArea);
        hiddenNotesFrame.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHiddenNotes(username, hiddenNotesTextArea.getText());
                JOptionPane.showMessageDialog(hiddenNotesFrame, "Hidden notes saved successfully!");
            }
        });

        JButton loadButton = new JButton("Load Hidden Notes");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHiddenNotes(username);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        hiddenNotesFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Prompt for password if accessing hidden notes for the first time
        if (hiddenNotesPassword == null) {
            String password = JOptionPane.showInputDialog(hiddenNotesFrame, "Enter a password for hidden notes:");
            String confirmPassword = JOptionPane.showInputDialog(hiddenNotesFrame, "Confirm password:");
            if (password.equals(confirmPassword)) {
                hiddenNotesPassword = password;
                saveHiddenNotesPassword(username, hiddenNotesPassword);
                hiddenNotesFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(hiddenNotesFrame, "Passwords do not match. Hidden notes access denied.");
            }
        } else {
            String password = JOptionPane.showInputDialog(hiddenNotesFrame, "Enter password for hidden notes:");
            if (password.equals(hiddenNotesPassword)) {
                hiddenNotesFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(hiddenNotesFrame, "Incorrect password. Hidden notes access denied.");
            }
        }
    }

    private void saveHiddenNotes(String username, String hiddenNotes) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hidden_notes VALUES (?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hiddenNotes);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHiddenNotes(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT hidden_note FROM hidden_notes WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                hiddenNotesTextArea.setText(resultSet.getString("hidden_note"));
            } else {
                JOptionPane.showMessageDialog(this, "Please save hidden notes first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveHiddenNotesPassword(String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hidden_notes_password VALUES (?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHiddenNotesPassword(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM hidden_notes_password WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                hiddenNotesPassword = resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTask() {
        String task = taskField.getText();
        String hours = (String) hoursComboBox.getSelectedItem();
        String minutes = (String) minutesComboBox.getSelectedItem();

        if (!task.isEmpty()) {
            String taskEntry = task + " - " + hours + ":" + minutes;
            todoListModel.addElement(taskEntry);
            saveTasks(username);
        }

        taskField.setText("");
    }

    private void completeTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedTask = todoListModel.get(selectedIndex);
            todoListModel.remove(selectedIndex);
            todoListModel.addElement("[Completed] " + selectedTask);
            saveTasks(username);
        }
    }

    private void removeTask() {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            todoListModel.remove(selectedIndex);
            saveTasks(username);
        }
    }

    private void loadTasks(String username) {
        if (todoListModel != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT task FROM tasks WHERE username = ?")) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
    
                // Clear the model before adding tasks
                todoListModel.clear();
    
                while (resultSet.next()) {
                    todoListModel.addElement(resultSet.getString("task"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("todoListModel is null. Make sure it's properly initialized.");
        }
    }
    
    private void saveTasks(String username) {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM tasks WHERE username = ?")) {
            deleteStatement.setString(1, username);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO tasks VALUES (?, ?)")) {
            insertStatement.setString(1, username);
            for (int i = 0; i < todoListModel.getSize(); i++) {
                insertStatement.setString(2, todoListModel.getElementAt(i));
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private String[] getHours() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        return hours;
    }

    private String[] getMinutes() {
        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        return minutes;
    }

    private class CompletedTaskRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value.toString().startsWith("[Completed]")) {
                renderer.setFont(renderer.getFont().deriveFont(Font.ITALIC));
                renderer.setForeground(Color.GRAY);
            }

            return renderer;
        }
    }

    private void checkNotifications() {
        LocalTime currentTime = LocalTime.now();
        for (int i = 0; i < todoListModel.getSize(); i++) {
            String task = todoListModel.getElementAt(i);
            String[] parts = task.split(" - ");
            if (parts.length == 2) {
                String time = parts[1];
                LocalTime taskTime = LocalTime.parse(time);
                if (currentTime.getHour() == taskTime.getHour() && currentTime.getMinute() == taskTime.getMinute()) {
                    showNotification("Task Reminder", "It's time to do: " + parts[0]);
                }
            }
        }
    }

    private void showNotification(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void openNotes(String username) {
        JFrame notesFrame = new JFrame("Notes");
        notesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        notesFrame.setSize(400, 300);
        notesFrame.setLocationRelativeTo(null);

        notesTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(notesTextArea);
        notesFrame.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNotes(username, notesTextArea.getText());
                JOptionPane.showMessageDialog(notesFrame, "Notes saved successfully!");
            }
        });

        JButton loadButton = new JButton("Load Notes");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadNotes(username);
            }
        });



        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        notesFrame.add(buttonPanel, BorderLayout.SOUTH);

        notesFrame.setVisible(true);
    }

    private void saveNotes(String username, String notes) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO notes VALUES (?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, notes);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNotes(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT note FROM notes WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                notesTextArea.setText(resultSet.getString("note"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    


public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            // Prompt the user to enter their username
            String username = JOptionPane.showInputDialog(null, "Enter your username:");
            TodoListGUI todoListGUI = new TodoListGUI(username);
        }
    });
}}