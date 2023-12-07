package TaskInfo;

import getsolution.GetSolution;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TaskInfo extends JFrame {
    private JComboBox<String> priorityComboBox;
    private JTextArea commentsArea;
    private final Object[] rowData;
    private JTextField searchField;
    private JButton searchButton;

    public TaskInfo(Object[] rowData, Consumer<Object[]> updateConsumer) {
        this.rowData = rowData;
        setTitle("Task Information");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        String[] labels = {"ID", "UPDATED", "STATUS", "TITLE", "COMMENTS", "PRIORITY", "ASSIGNED TO", "REQUESTER"};

        for (int i = 0; i < labels.length; i++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.add(new JLabel(labels[i] + ": "));

            if ("PRIORITY".equals(labels[i])) {
                priorityComboBox = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});
                priorityComboBox.setEditable(false);
                if (rowData[i] != null) {
                    priorityComboBox.setSelectedItem(rowData[i].toString());
                }
                rowPanel.add(priorityComboBox);
            } else {
                JLabel dataLabel = new JLabel(rowData[i] != null ? rowData[i].toString() : "");
                rowPanel.add(dataLabel);
            }
            detailsPanel.add(rowPanel);
        }

        searchField = new JTextField(15);
        searchButton = new JButton("Search on Web");
        searchButton.addActionListener(GetSolution.getSearchActionListener(searchField));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(detailsPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        commentsArea = new JTextArea(5, 20);
        commentsArea.setText(rowData[4] != null ? rowData[4].toString() : "");
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane commentsScrollPane = new JScrollPane(commentsArea);

        JTextArea newCommentArea = new JTextArea("Add new comment");
        newCommentArea.setForeground(Color.GRAY);
        newCommentArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (newCommentArea.getText().equals("Add new comment")) {
                    newCommentArea.setText("");
                    newCommentArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (newCommentArea.getText().isEmpty()) {
                    newCommentArea.setForeground(Color.GRAY);
                    newCommentArea.setText("Add new comment");
                }
            }
        });
        newCommentArea.setLineWrap(true);
        newCommentArea.setWrapStyleWord(true);
        JScrollPane newCommentScrollPane = new JScrollPane(newCommentArea);

        JButton submitButton = new JButton("Submit Comment");
        submitButton.addActionListener(e -> {
            String newComment = newCommentArea.getText().trim();
            if (!newComment.isEmpty() && !newComment.equals("Add new comment")) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                String formattedDate = now.format(formatter);
                String commentHeader = "\n******************************************************\nNEW UPDATE            " + formattedDate + "\n******************************************************\n";
                String fullComment = commentHeader + newComment + "\n";

                commentsArea.append(fullComment);
                newCommentArea.setText("Add new comment");
                newCommentArea.setForeground(Color.GRAY);
            }
        });

        JButton updateButton = new JButton("Update Task");
        updateButton.addActionListener(e -> {
            rowData[4] = commentsArea.getText();
            rowData[5] = priorityComboBox.getSelectedItem().toString();
            updateConsumer.accept(rowData);
            JOptionPane.showMessageDialog(this, "Task updated successfully!");
            dispose(); // Close the window after update
        });

        JPanel newCommentPanel = new JPanel(new BorderLayout());
        newCommentPanel.add(newCommentScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        buttonPanel.add(updateButton);

        newCommentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(commentsScrollPane, BorderLayout.CENTER);
        add(newCommentPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Make the window visible at the end of the constructor
        setVisible(true);
    }
}
