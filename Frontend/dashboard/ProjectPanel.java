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
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ProjectPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton; // New button for refreshing
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
        refreshButton = new JButton("Refresh"); // Added refresh button

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addProjectButton);
        searchPanel.add(refreshButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> search());
        addProjectButton.addActionListener(e -> addNewProject());
        refreshButton.addActionListener(e -> refresh());
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

        // Set the project ID as a client property for the tab
        panel.putClientProperty("projectId", projectId);

        return panel;
    }

    private void search() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 0) {
            // "All Projects" tab
            TableRowSorter<DefaultTableModel> mainSorter = rowSorters.get(-1);
            if (mainSorter != null) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    mainSorter.setRowFilter(null);
                } else {
                    mainSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        } else {
            // Individual project tab
            try {
                int projectId = (int) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getClientProperty("projectId");
                DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getComponent(0)).getViewport().getView()).getModel();
                Object[][] updatedTasks = SharedData.getTasksByProjectId(projectId);
                model.setDataVector(updatedTasks, SharedData.columnNames);
            } catch (NumberFormatException | NullPointerException e) {
                // Handle non-numeric project ID or null project ID (e.g., "Project Alpha")
                // Add your logic here or simply skip the tab
                System.out.println("Skipping tab with invalid project ID: " + tabbedPane.getTitleAt(selectedIndex));
            }
        }
    }

    // New method for refreshing the project list
    private void refresh() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 0) {
            // "All Projects" tab
            // Fetch updated project details from the backend (Assuming you have a method for this in SharedData)
            Object[][] updatedProjects = SharedData.getAllProjectDetails();
            mainTableModel.setDataVector(updatedProjects, new String[]{"Project ID", "Project Name", "Start Date", "Completion Date"});

            // Add new tabs for any newly created projects
            for (Object[] project : updatedProjects) {
                Integer projectId = (Integer) project[0];
                String projectName = (String) project[1];

                // Check if the project ID already exists in the tabbed pane
                boolean tabExists = false;
                for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                    JPanel tabPanel = (JPanel) tabbedPane.getComponentAt(i);
                    int existingProjectId = (int) tabPanel.getClientProperty("projectId");
                    if (existingProjectId == projectId) {
                        tabExists = true;
                        break;
                    }
                }

                // If the tab does not exist, create a new tab
                if (!tabExists) {
                    tabbedPane.addTab(projectName, createTabContentPanel(projectId));
                }
            }
        } else {
            // Individual project tab
            try {
                int projectId = (int) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getClientProperty("projectId");
                DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getComponent(0)).getViewport().getView()).getModel();
                Object[][] updatedTasks = SharedData.getTasksByProjectId(projectId);
                model.setDataVector(updatedTasks, SharedData.columnNames);
            } catch (NumberFormatException | NullPointerException e) {
                // Handle non-numeric project ID or null project ID (e.g., "Project Alpha")
                // Add your logic here or simply skip the tab
                System.out.println("Skipping tab with invalid project ID: " + tabbedPane.getTitleAt(selectedIndex));
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
            createProject(projectName, startDate, completionDate);
        }
    }

    private void createProject(String projectName, String startDate, String completionDate) {
        try {
            URL url = new URL("http://192.168.1.22:8080/api/v1/project/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXRpa3ZpZzIyQGdtYWlsLmNvbSIsInVzZXJJZCI6NSwiaWF0IjoxNzAyMjM5Mzc1LCJleHAiOjE3MDIzMjU3NzV9.bqfDhrfV1oKaMG2cvok-tSuW4JwHOBz57m3Ap0TvLI0");

            conn.setDoOutput(true);

            // Adjust the date format and JSON structure to match the desired format
            String jsonInputString = String.format("{\"projectName\": \"%s\", \"startDate\": \"%s\", \"completionDate\": \"%s\"}",
                                                   projectName, startDate.replaceAll("-", "/"), completionDate.replaceAll("-", "/"));
            System.out.println(jsonInputString);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Handle successful response
                refresh();
                JOptionPane.showMessageDialog(null, "Project created successfully.");
            } else {
                // Handle server error
                JOptionPane.showMessageDialog(null, "Error creating project. Server returned: " + responseCode);
            }
        } catch (Exception e) {
        	StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage() + "\n\n" + sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
