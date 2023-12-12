package dashboard;

import shared.JwtStorage;
import shared.SharedData;
import shared.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import TaskInfo.TaskInfoPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import TaskInfo.TaskInfoPanel;

public class  ProjectPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton; // New button for refreshing
    private JButton closeButton;
    private JButton addProjectButton;
    private JButton addTaskButton;
    private Map<Integer, TableRowSorter<DefaultTableModel>> rowSorters;

    private ArrayList<String> openedTabs = new ArrayList<>();
    private DefaultTableModel mainTableModel; // Model for the main tab

    //    private ArrayList<Object[]> allProjects = new ArrayList<>();
    Object[][] allProjects;
    public ProjectPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        rowSorters = new HashMap<>();
        allProjects = SharedData.getAllProjectDetails();

        // Add the main tab
        addMainTab();

        add(tabbedPane, BorderLayout.CENTER);

        // Create a panel for search and add new project
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        addProjectButton = new JButton("Add New Project");
        addTaskButton = new JButton("Add New Task"); 
        refreshButton = new JButton("Refresh"); 
        closeButton = new JButton("Close");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addProjectButton);
        searchPanel.add(addTaskButton);
        searchPanel.add(refreshButton);
        searchPanel.add(closeButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> search());
        addProjectButton.addActionListener(e -> addNewProject());
        addTaskButton.addActionListener(e -> showAddTaskDialog());
        refreshButton.addActionListener(e -> refresh());
        closeButton.setVisible(false);
        addTaskButton.setEnabled(false);

        closeButton.addActionListener(e -> closeCurrentTab());

        tabbedPane.addChangeListener((e) -> {
            if(tabbedPane.getSelectedIndex() == 0){
                closeButton.setVisible(false);
                addTaskButton.setEnabled(false);
                addProjectButton.setEnabled(true);
            }else{
                closeButton.setVisible(true);
                addTaskButton.setEnabled(true);
                addProjectButton.setEnabled(false);
            }
        });
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

        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedProject = (String)mainTable.getValueAt(mainTable.getSelectedRow() ,1);
                if(e.getClickCount() == 2){
                    int idx = openedTabs.indexOf(selectedProject);
                    if( idx != -1){
                        System.out.println(idx);
                        tabbedPane.setSelectedIndex(idx+1);
                    }else{

                        int tabs = tabbedPane.getTabCount();

                        Object[] project = null;

                        for(Object[] p : allProjects){
                            if(p[1].equals(selectedProject)){
                                project = p;
                            }
                        }
                        tabbedPane.addTab((String)project[1], createTabContentPanel(project));
                        openedTabs.add((String)project[1]);
                    }
                }
            }
        });

        // Add individual project tabs
        for (Object[] project : allProjects) {
            Integer projectId = (Integer) project[0];
            String projectName = (String) project[1];
            JPanel projectTabPanel = createTabContentPanel(project);
            tabbedPane.addTab(projectName, projectTabPanel);

            // Add double-click listener for individual project tabs
            JTable projectTable = findTable(projectTabPanel);
            projectTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = projectTable.getSelectedRow();
                        if (row != -1) {
                            // Extract the selected task data
                            Object[] taskData = new Object[projectTable.getColumnCount()];
                            for (int i = 0; i < projectTable.getColumnCount(); i++) {
                                taskData[i] = projectTable.getValueAt(projectTable.convertRowIndexToModel(row), i);
                            }

                            // Create and show the TaskInfoPanel in a dialog
                            TaskInfoPanel taskInfoPanel = new TaskInfoPanel(taskData);
                            JOptionPane.showMessageDialog(ProjectPanel.this, taskInfoPanel, "Task Info", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            });
        }
    }



    private JPanel createTabContentPanel(Object[] project) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel projectDetails = new JPanel(new GridLayout(1, 4));
        int projectId = (int) project[0];
        projectDetails.setBorder(new EmptyBorder(25, 25, 25, 25));
        projectDetails.add(new JLabel("Project ID: " + projectId));
        projectDetails.add(new JLabel("Project Name: " + project[1]));
        projectDetails.add(new JLabel("Start Date: " + project[2]));
        projectDetails.add(new JLabel("Completion Date: " + project[3]));
        String[] columnNames = SharedData.columnNames;
        Object[][] rowData = SharedData.getTasksByProjectId(projectId);

        if (rowData.length == 0) {
            // Display "No tasks defined" message when there are no tasks
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
            panel.add(projectDetails, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JLabel noTasksLabel = new JLabel("No tasks defined");
//            noTasksLabel.setHorizontalAlignment(JLabel.CENTER);
            noTasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noTasksLabel, BorderLayout.SOUTH);

        } else {
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
            panel.add(projectDetails, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        }
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
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);

                if (selectedComponent instanceof JPanel) {
                    JPanel selectedPanel = (JPanel) selectedComponent;

                    // Find the JTable within the selectedPanel
                    JTable table = findTable(selectedPanel);

                    if (table != null) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();

                        // Example: filtering based on user input
                        String text = searchField.getText();
                        TableRowSorter<DefaultTableModel> sorter = rowSorters.get((int) selectedPanel.getClientProperty("projectId"));
                        if (sorter != null) {
                            if (text.trim().length() == 0) {
                                sorter.setRowFilter(null);
                            } else {
                                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                            }
                        }
                    }
                }
            } catch (NumberFormatException | NullPointerException e) {
                // Handle non-numeric project ID or null project ID
                System.out.println("Skipping tab with invalid project ID: " + tabbedPane.getTitleAt(selectedIndex));
            }
        }
    }

    // Helper method to find JTable within a container
    private JTable findTable(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTable) {
                return (JTable) component;
            } else if (component instanceof Container) {
                JTable table = findTable((Container) component);
                if (table != null) {
                    return table;
                }
            }
        }
        return null;
    }


    // New method for refreshing the project list
    void refresh() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 0) {
            // "All Projects" tab
            // Fetch updated project details from the backend
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

                    tabbedPane.addTab(projectName, createTabContentPanel(project));
                }
            }
        } else {
            // Individual project tab
            try {
                int projectId = (int) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getClientProperty("projectId");
                
                // Update the project details in the main tab
                Object[][] updatedProjects = SharedData.getAllProjectDetails();
                mainTableModel.setDataVector(updatedProjects, new String[]{"Project ID", "Project Name", "Start Date", "Completion Date"});

                // Find and update the specific project tab
                for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                    JPanel tabPanel = (JPanel) tabbedPane.getComponentAt(i);
                    int existingProjectId = (int) tabPanel.getClientProperty("projectId");

                    if (existingProjectId == projectId) {
                        DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) tabPanel.getComponent(1)).getViewport().getView()).getModel();
                        Object[][] updatedTasks = SharedData.getTasksByProjectId(projectId);
                        model.setDataVector(updatedTasks, SharedData.columnNames);
                        break;
                    }
                }
            } catch (NumberFormatException | NullPointerException e) {
                // Handle non-numeric project ID or null project ID
                System.out.println("Skipping tab with invalid project ID: " + tabbedPane.getTitleAt(selectedIndex));
            }
        }
    }
    private void closeCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex != -1 && selectedIndex != 0) {
            System.out.println(((JPanel)tabbedPane.getComponentAt(selectedIndex)).getComponent(0).getName());
            String projectId = tabbedPane.getTitleAt(selectedIndex);
            System.out.println(projectId);
            System.out.println(projectId);
            tabbedPane.remove(selectedIndex);
            openedTabs.remove(projectId);
            // Additional logic if needed for handling the closed tab
        }
    }


    private void addNewProject() {
        JTextField projectNameField = new JTextField(20);
        JTextField startDateField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Project Name:"));
        panel.add(projectNameField);
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
//        panel.add(new JLabel("Completion Date (YYYY-MM-DD):"));
//        panel.add(completionDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Project Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String projectName = projectNameField.getText();
            String startDate = startDateField.getText();

            // Make POST request to create a new project
            createProject(projectName, startDate);
        }
    }
    private void showAddTaskDialog() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 1) {
            // Initialize an empty data array for a new task
            Object[] emptyTaskData = new Object[]{null, null, "", "", "", "", "", "", "", "", "", ""};

            // Create a new TaskInfoPanel with the empty data array
            TaskInfoPanel taskInfoPanel = new TaskInfoPanel(emptyTaskData);

            // Show the TaskInfoPanel in a dialog
            JOptionPane.showMessageDialog(this, taskInfoPanel, "Add New Task", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Select a project to add a task to.");
        }
    }

    private void createProject(String projectName, String startDate) {
        try {
            URL url = new URL(Constants.BACKEND_URL + "/api/v1/project/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + JwtStorage.getJwtToken());

            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"projectName\": \"%s\", \"startDate\": \"%s\" }",
                    projectName, startDate.replaceAll("-", "/"));

            try (OutputStream os = conn.getOutputStream()) {
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

//    private void showAddTaskDialog() {
//        int selectedIndex = tabbedPane.getSelectedIndex();
//        if (selectedIndex >= 1) {
//            int projectId = (int) ((JPanel) tabbedPane.getComponentAt(selectedIndex)).getClientProperty("projectId");
//            TaskDialog taskDialog = new TaskDialog((JFrame) SwingUtilities.getWindowAncestor(this), projectId);
//            taskDialog.showDialog();
//        } else {
//            JOptionPane.showMessageDialog(this, "Select a project to add a task to.");
//        }
//    }
}
