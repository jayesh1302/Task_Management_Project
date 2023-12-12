package dashboard;

import javax.swing.*;

import shared.Constants;
import shared.JwtStorage;
import services.CreateTask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;

public class TaskDialog extends JDialog {
    private JTextField taskNameField;
    private JComboBox<String> taskPriorityComboBox;
    private JComboBox<String> taskStatusComboBox;
    private JTextField endDateField;
    private JTextField dueDateField;

    public TaskDialog(JFrame parent, int projectId) {
        super(parent, "Add New Task", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));

        taskNameField = new JTextField(20);
        taskPriorityComboBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        taskStatusComboBox = new JComboBox<>(new String[]{"IN_PROGRESS", "RESOLVED", "UNRESOLVED", "ESCALATED"});
        endDateField = new JTextField(10);
        dueDateField = new JTextField(10);

        inputPanel.add(new JLabel("Task Name:"));
        inputPanel.add(taskNameField);
        inputPanel.add(new JLabel("Task Priority:"));
        inputPanel.add(taskPriorityComboBox);
        inputPanel.add(new JLabel("Task Status:"));
        inputPanel.add(taskStatusComboBox);
        inputPanel.add(new JLabel("End Date (DD/MM/YYYY):"));
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("Due Date (DD/MM/YYYY):"));
        inputPanel.add(dueDateField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask(projectId);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addTask(int projectId) {
        // Get user input
        String taskName = taskNameField.getText();
        String taskPriority = (String) taskPriorityComboBox.getSelectedItem();
        String taskStatus = (String) taskStatusComboBox.getSelectedItem();
        String endDate = endDateField.getText();
        String dueDate = dueDateField.getText();

        // Get current user ID (you should implement this)
        int assignedToUserId = getCurrentUserId();

        // Get current date in the required format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        // Create a JSON object with the task data
        String taskJson = String.format("{\"taskName\": \"%s\", \"taskPriority\": \"%s\", \"taskStatus\": \"%s\", " +
                "\"startDate\": \"%s\", \"endDate\": \"%s\", \"dueDate\": \"%s\", \"lastUpdated\": \"%s\", " +
                "\"projectId\": %d, \"assignedTo\": %d, \"assignedBy\": %d}",
                taskName, taskPriority, taskStatus, currentDate, endDate, dueDate, currentDate, projectId,
                assignedToUserId, assignedToUserId);
        System.out.println(taskJson);

        // Make a POST request to create the task using the CreateTask class
        boolean taskCreated = CreateTask.sendPostRequest(Constants.BACKEND_URL + "/api/v1/task/create", taskJson, JwtStorage.getJwtToken());

        if (taskCreated) {
            JOptionPane.showMessageDialog(this, "Task created successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error creating task.");
        }
    }

    private int getCurrentUserId() {
        // Implement this method to get the current user's ID
        // You may retrieve it from the authentication system or user session
        return 1; // Replace with actual implementation
    }

    public void showDialog() {
        setVisible(true);
    }
}
