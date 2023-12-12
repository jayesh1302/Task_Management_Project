package TaskInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskInfoPanel extends JPanel {
    private JTextField taskIdField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField dueDateField;
    private JTextField lastUpdateField;
    private JTextField titleField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> priorityComboBox;
    private JTextField assignedToField;
    private JTextField requestedByField;
    private JScrollPane commentsTextArea;
//    private JTextArea commentsTextArea;
    private JButton updateButton;

    public TaskInfoPanel(Object[] taskData) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set the initial gridbag constraints
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Initialize components with a wider column width
        int textFieldColumnWidth = 30;
        taskIdField = new JTextField(textFieldColumnWidth);
        startDateField = new JTextField(textFieldColumnWidth);
        endDateField = new JTextField(textFieldColumnWidth);
        dueDateField = new JTextField(textFieldColumnWidth);
        lastUpdateField = new JTextField(textFieldColumnWidth);
        titleField = new JTextField(textFieldColumnWidth);
        statusComboBox = new JComboBox<>(new String[]{"IN_PROGRESS", "RESOLVED", "UNRESOLVED", "ESCALATED"});
        priorityComboBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        assignedToField = new JTextField(textFieldColumnWidth);
        requestedByField = new JTextField(textFieldColumnWidth);
        commentsTextArea = new JScrollPane();
        commentsTextArea.setPreferredSize(new Dimension(400, 200));
        commentsTextArea.setBackground(Color.WHITE);
        commentsTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

//        commentsTextArea = new JTextArea(5, textFieldColumnWidth);
        updateButton = new JButton("Update");
        
        assignedToField.setEditable(false);
        requestedByField.setEditable(false);
        
        // Correct data initialization
        taskIdField.setText(taskData[0] != null ? taskData[1].toString() : "");
        startDateField.setText(taskData[2] != null ? taskData[2].toString() : "");
        endDateField.setText(taskData[3] != null ? taskData[3].toString() : "");
        dueDateField.setText(taskData[4] != null ? taskData[4].toString() : "");
        lastUpdateField.setText(taskData[5] != null ? taskData[5].toString() : "");
        titleField.setText(taskData[7] != null ? taskData[7].toString() : "");
        // Set the status dropdown selection correctly
        if (taskData[6] != null) {
            statusComboBox.setSelectedItem(taskData[6].toString());
        } else {
            statusComboBox.setSelectedIndex(-1); // No selection
        }
        if (taskData[9] != null) {
            priorityComboBox.setSelectedItem(taskData[9].toString());
        } else {
        	priorityComboBox.setSelectedIndex(-1); // No selection
        }
        assignedToField.setText(taskData[10] != null ? taskData[10].toString() : "");
        requestedByField.setText(taskData[11] != null ? taskData[11].toString() : "");
        commentsTextArea.setViewportView(createCommentPane());

        // Ensure comments are filled correctly
//        commentsTextArea.setText(taskData[8] != null ? taskData[8].toString() : "here");

        // Add components with their labels
        addFieldWithLabel("TASK ID:", taskIdField, 0, gbc);
        addFieldWithLabel("START DATE:", startDateField, 1, gbc);
        addFieldWithLabel("END DATE:", endDateField, 2, gbc);
        addFieldWithLabel("DUE DATE:", dueDateField, 3, gbc);
        addFieldWithLabel("LAST UPDATE:", lastUpdateField, 4, gbc);
        addFieldWithLabel("TITLE:", titleField, 5, gbc);
        addFieldWithLabel("STATUS:", statusComboBox, 6, gbc);
        addFieldWithLabel("PRIORITY:", priorityComboBox, 7, gbc);
        addFieldWithLabel("ASSIGNED TO:", assignedToField, 8, gbc);
        addFieldWithLabel("REQUESTED BY:", requestedByField, 9, gbc);
        addFieldWithLabel("COMMENTS:", commentsTextArea, 10, gbc);

        // Adding the update button at the bottom
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(updateButton, gbc);
        
        updateButton.addActionListener(e -> {
            int taskId = Integer.parseInt(taskIdField.getText());
            String taskName = titleField.getText();
            String taskPriority = priorityComboBox.getSelectedItem().toString();
            String taskStatus = statusComboBox.getSelectedItem().toString();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            String dueDate = dueDateField.getText();
            String lastUpdated = lastUpdateField.getText();

            services.UpdateTaskService.updateTask(taskId, taskName, taskPriority, taskStatus, startDate, endDate, dueDate, lastUpdated);
        });
    }

    private JPanel createCommentPane(){
        JPanel cPanel = new JPanel(new GridLayout(0,1));

        for(int i=0; i<20; i++){
            JPanel comment = new JPanel(new BorderLayout());
            comment.setBackground(Color.white);

            JTextArea commentText = new JTextArea("Add comment here Add comment here Add comment here Add comment here");
            comment.setBorder(new EmptyBorder(10,10,0,10));
            commentText.setEditable(false);
            commentText.setLineWrap(true);
            commentText.setWrapStyleWord(true);

            commentText.setColumns(20);

            comment.add(new JLabel("Date: "), BorderLayout.NORTH);
            comment.add(commentText, BorderLayout.CENTER);

            cPanel.add(comment);
        }
        return cPanel;
    }

    private void addFieldWithLabel(String labelText, Component field, int yPos, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.EAST;
        add(label, gbc);
        gbc.gridx = 1;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        add(field, gbc);
    }
    
    
    
}
