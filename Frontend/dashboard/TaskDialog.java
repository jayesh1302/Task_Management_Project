package dashboard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import shared.Constants;
import shared.JwtStorage;
import services.CreateTask;

public class TaskDialog extends JDialog {
    private JTextField taskNameField;
    private JComboBox<String> taskPriorityComboBox;
    private JComboBox<String> taskStatusComboBox;
    private JTextField endDateField;
    private JTextField dueDateField;
    private JButton addButton;

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

        // Add DocumentListener to text fields
        taskNameField.getDocument().addDocumentListener(new FieldDocumentListener());
        endDateField.getDocument().addDocumentListener(new FieldDocumentListener());
        dueDateField.getDocument().addDocumentListener(new FieldDocumentListener());

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
        addButton = new JButton("Add Task");
        JButton cancelButton = new JButton("Cancel");

        addButton.setEnabled(false); // Initially disable the button

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

            // Call the refresh method in the parent ProjectPanel
            if (getParent() instanceof ProjectPanel) {
                ((ProjectPanel) getParent()).refresh();
            }

            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error creating task.");
        }
        // Call refresh function of ProjectPanel
    }

    private int getCurrentUserId() {
        // Implement this method to get the current user's ID
        // You may retrieve it from the authentication system or user session
        return 1; // Replace with actual implementation
    }

    public void showDialog() {
        setVisible(true);
    }

    // DocumentListener to trigger validation when text fields change
    private class FieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            validateFields();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validateFields();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validateFields();
        }

        private void validateFields() {
            boolean enableButton = !taskNameField.getText().isEmpty()
                    && !endDateField.getText().isEmpty()
                    && !dueDateField.getText().isEmpty();

            addButton.setEnabled(enableButton);
        }
    }
}
