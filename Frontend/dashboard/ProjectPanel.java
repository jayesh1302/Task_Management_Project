package dashboard;

import shared.SharedData;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProjectPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton; // New button for refreshing
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

        // Create a panel for search and refresh
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh"); // Added refresh button

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton); // Added refresh button to the panel
        add(searchPanel, BorderLayout.SOUTH);

        // Add action listeners for search and refresh functionality
        searchButton.addActionListener(e -> search());
        refreshButton.addActionListener(e -> refresh()); // Added action for the refresh button
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new ProjectPanel());
            frame.setPreferredSize(new Dimension(800, 600));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
