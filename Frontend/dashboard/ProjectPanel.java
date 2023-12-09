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
    private Map<Integer, String> projectNames;
    private JTextField searchField;
    private JButton searchButton;
    private Map<Integer, TableRowSorter<DefaultTableModel>> rowSorters;
    private DefaultTableModel mainTableModel; // Model for the main tab

    public ProjectPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        projectNames = getProjectNamesFromDatabase();
        rowSorters = new HashMap<>();

        // Add the main tab
        addMainTab();

        // Initialize the tabbed pane with tabs for individual projects
        for (Integer projectId : projectNames.keySet()) {
            String projectName = projectNames.get(projectId);
            tabbedPane.addTab(projectName, createTabContentPanel(projectId));
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Create a panel for search
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> search());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
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
        rowSorters.put(tabbedPane.getTabCount() - 1, sorter); 
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Map<Integer, String> getProjectNamesFromDatabase() {
        // Dummy implementation, replace with actual database query handling code
        Map<Integer, String> projectNames = new HashMap<>();
        projectNames.put(1, "Project Alpha");
        projectNames.put(2, "Project Beta");
        projectNames.put(3, "Project Gamma");
        projectNames.put(4, "Project Delta");
        projectNames.put(5, "Project Epsilon");
        return projectNames;
    }
}
