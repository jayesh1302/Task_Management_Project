package dashboard;

import shared.SharedData;
import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProjectPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addProjectButton;
    private Map<Integer, TableRowSorter<DefaultTableModel>> rowSorters;
    private DefaultTableModel mainTableModel; // Model for the main tab

    public ProjectPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        rowSorters = new HashMap<>();

        // Add the main tab
        addMainTab();

        // Initialize the tabbed pane with tabs for projects from getAllProjectDetails
        Object[][] allProjects = SharedData.getAllProjectDetails();
        for (Object[] project : allProjects) {
            Integer projectId = (Integer) project[0];
            String projectName = (String) project[1];
            tabbedPane.addTab(projectName, createTabContentPanel(projectId));
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Create a panel for search and add new project
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        addProjectButton = new JButton("Add New Project");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addProjectButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> search());
        addProjectButton.addActionListener(e -> addNewProject());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });
    }

    private void addMainTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        String[] mainColumnNames = {"Project ID", "Project Name", "Start Date", "Completion Date"};
        Object[][] mainRowData = SharedData.getAllProjectDetails();

        mainTableModel = new DefaultTableModel(mainRowData, mainColumnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable mainTable = new JTable(mainTableModel);
        TableRowSorter<DefaultTableModel> mainSorter = new TableRowSorter<>(mainTableModel);
        rowSorters.put(-1, mainSorter);
        mainTable.setRowSorter(mainSorter);

        JScrollPane mainScrollPane = new JScrollPane(mainTable);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);
        tabbedPane.insertTab("All Projects", null, mainPanel, null, 0);
    }

    private JPanel createTabContentPanel(int projectId) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = SharedData.columnNames;
        Object[][] rowData = SharedData.getTasksByProjectId(projectId);

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        rowSorters.put(projectId, sorter);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void search() {
        TableRowSorter<DefaultTableModel> currentSorter = rowSorters.get(tabbedPane.getSelectedIndex());
        if (currentSorter != null) {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                currentSorter.setRowFilter(null);
            } else {
                currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }

    private void addNewProject() {
        JTextField projectNameField = new JTextField(20);
        JTextField startDateField = new JTextField(20);
        JTextField completionDateField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Project Name:"));
        panel.add(projectNameField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
        panel.add(new JLabel("Completion Date (YYYY-MM-DD):"));
        panel.add(completionDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Project Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String projectName = projectNameField.getText();
            String startDate = startDateField.getText();
            String completionDate = completionDateField.getText();

            // Make POST request to create a new project
            createProject();
        }
    }

    private void createProject() {
        try {
            URL url = new URL("http://10.18.249.69/api/v1/project/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String completionDate = "".equals("") ? null : "\"\""; // Replace with actual variable or logic
            String jsonInputString = String.format(
                    "{\"projectName\": \"%s\", \"startDate\": \"%s\", \"completionDate\": %s, \"userId\": \"%s\"}", 
                    "testJayesh", "03/29/2023", "03/29/2024", "1"
            );

            System.out.println("Making POST request with data:");
            System.out.println(jsonInputString);

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                // Handle successful response
                JOptionPane.showMessageDialog(null, "Project created successfully.");
            } else {
                // Handle server error
                JOptionPane.showMessageDialog(null, "Error creating project. Server returned: " + responseCode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }



}
