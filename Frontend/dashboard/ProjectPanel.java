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

    public ProjectPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        projectNames = getProjectNamesFromDatabase();
        rowSorters = new HashMap<>();

        // Initialize the tabbed pane with tabs
        for (Integer projectId : projectNames.keySet()) {
            String projectName = projectNames.get(projectId);
            tabbedPane.addTab(projectName, createTabContentPanel(projectId));
        }

        // Add the tabbed pane to the center of the ProjectPanel
        add(tabbedPane, BorderLayout.CENTER);

        // Create a panel for search
        JPanel searchPanel = new JPanel();
        // Use FlowLayout for the searchPanel to align components horizontally
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Left-align components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        // Add searchField and searchButton to the searchPanel
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add the search panel to the bottom of the ProjectPanel
        add(searchPanel, BorderLayout.SOUTH);

        // Action listener for search button
        searchButton.addActionListener(e -> search());

        // Document listener for search field to enable real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
        });
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
                // make the cells non-editable or implement your own logic
                return false;
            }
        };

        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        rowSorters.put(tabbedPane.getTabCount() - 1, sorter); // Store sorter for each tab
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private Map<Integer, String> getProjectNamesFromDatabase() {
        // This should be replaced with actual database query handling code
        Map<Integer, String> projectNames = new HashMap<>();
        projectNames.put(1, "Project Alpha");
        projectNames.put(2, "Project Beta");
        projectNames.put(3, "Project Gamma");
        projectNames.put(4, "Project Delta");
        projectNames.put(5, "Project Epsilon");
        return projectNames;
    }
}
