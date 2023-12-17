package TaskInfo;

import com.toedter.calendar.JDateChooser;
import dashboard.ProjectPanel;
import dashboard.TaskDialog;
import shared.JwtStorage;
import shared.SharedData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTaskInfoPanel extends JPanel {
    private JTextField taskIdField;
    private JDateChooser startDateField;
    private JDateChooser endDateField;
    private JDateChooser dueDateField;
    private JTextField titleField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> priorityComboBox;
    private JTextField assignedToField;
    private JTextField requestedByField;
    private JScrollPane commentsTextArea;
    private JButton addTaskButton;
    private JTextField projectId;

    private JPanel addCommentPanel;

    private Object[][] comments;

    private Date parseDate(String dateString)  {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }


    public NewTaskInfoPanel(Object[] taskData)  {
        if(taskData[1] != null) comments = SharedData.getAllComments(taskData[1].toString());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set the initial gridbag constraints
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Initialize components with a wider column width
        int textFieldColumnWidth = 30;
        taskIdField = new JTextField(textFieldColumnWidth);
        startDateField = new JDateChooser();
        endDateField = new JDateChooser();
        dueDateField = new JDateChooser();
        projectId = new JTextField(textFieldColumnWidth);
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
        addTaskButton = new JButton("Add Task");
        
        assignedToField.setEditable(true);
        requestedByField.setEditable(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Correct data initialization
        taskIdField.setText(taskData[0] != null ? taskData[1].toString() : "");
        startDateField.setDate(taskData[2].equals("") ? null : parseDate(taskData[2].toString()));
        endDateField.setDate(taskData[3].equals("") ? null : parseDate(taskData[3].toString()));
        dueDateField.setDate(taskData[4].equals("") ? null : parseDate(taskData[2].toString()));
        titleField.setText(taskData[7] != null ? taskData[7].toString() : "");
        titleField.setText(taskData[8] != null ? taskData[8].toString() : "");
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
        if(taskData[1] != null)
            commentsTextArea.setViewportView(createCommentPane());

        // Ensure comments are filled correctly
//        commentsTextArea.setText(taskData[8] != null ? taskData[8].toString() : "here");


        // Add components with their labels
        addFieldWithLabel("TASK ID:", taskIdField, 0, gbc);
        addFieldWithLabel("START DATE:", startDateField, 1, gbc);
        addFieldWithLabel("END DATE:", endDateField, 2, gbc);
        addFieldWithLabel("DUE DATE:", dueDateField, 3, gbc);
        addFieldWithLabel("TITLE:", titleField, 5, gbc);
        addFieldWithLabel("PROJECT ID:", projectId, 6, gbc);
        addFieldWithLabel("STATUS:", statusComboBox, 7, gbc);
        addFieldWithLabel("PRIORITY:", priorityComboBox, 8, gbc);
        addFieldWithLabel("ASSIGNED TO:", assignedToField, 9, gbc);
        addFieldWithLabel("REQUESTED BY:", requestedByField, 10, gbc);
        taskIdField.setEditable(false);
        if(taskData[1] != null )
            addFieldWithLabel("COMMENTS:", commentsTextArea, 11, gbc);
        if(taskData[1] != null) {
            addCommentPanel = new JPanel();
            JTextField commentField = new JTextField();
            commentField.setColumns(20);
            addCommentPanel.add(commentField);
            JButton sendBtn = new JButton("Send");
            sendBtn.addActionListener(e -> {
                String comment = commentField.getText();
                addComment(comment, taskData[1].toString());
            });
            addCommentPanel.add(sendBtn);
//            add(addCommentPanel);
            addFieldWithLabel("", addCommentPanel, 12, gbc);
        }

        // Adding the update button at the bottom
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addTaskButton, gbc);
        
        addTaskButton.addActionListener(e -> {
            int taskId = Integer.parseInt("1");
            String taskName = titleField.getText();
            String taskPriority = priorityComboBox.getSelectedItem().toString();
            String taskStatus = statusComboBox.getSelectedItem().toString();
            Date startDate = startDateField.getDate();
            Date endDate = endDateField.getDate();
            Date dueDate = dueDateField.getDate();
            boolean success = services.newTaskService.createTask(taskName, taskPriority, taskStatus, startDate, endDate, dueDate);
            if(success){
                SwingUtilities.getWindowAncestor((JButton)e.getSource()).dispose();
                ProjectPanel.refresh();
            }
        });
    }

    private JPanel createCommentPane() {
        JPanel cPanel = new JPanel();
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
        cPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Add a small border to the entire panel
        for (int i = 0; i < comments.length; i++) {
            JPanel comment = new JPanel(new BorderLayout());
            comment.setBackground(Color.white);
            comment.setBorder(new EmptyBorder(5, 5, 5, 5)); // Adjust the border for each comment

            JTextArea commentText = new JTextArea(comments[i][0].toString());
            commentText.setEditable(false);
            commentText.setLineWrap(true);
            commentText.setWrapStyleWord(true);

            commentText.setColumns(20);

            comment.add(new JLabel(comments[i][1].toString()), BorderLayout.NORTH);
            comment.add(commentText);

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

    private void addComment(String comment, String taskId) {
        String jwtToken = JwtStorage.getJwtToken();
        try {

            URL url = new URL("http://localhost:8080/api/v1/task/addComment/" + taskId);
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"comment\": \"%s\"}", comment);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                comments = SharedData.getAllComments(taskId);
                commentsTextArea.setViewportView(createCommentPane());
                JScrollBar verticalScrollBar = commentsTextArea.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            } else {
                System.out.println("Error adding comment");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
