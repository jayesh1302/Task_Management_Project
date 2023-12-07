package dashboard;

import TaskInfo.TaskInfo;
import shared.SharedData;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class DashboardPanel extends JPanel {
    private JTable dataTable;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private DefaultTableModel tableModel;

    public DashboardPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JButton newTaskButton = new JButton("New Task");
        newTaskButton.addActionListener(e -> openNewTaskInfo());

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 0);
        add(newTaskButton, constraints);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 10;
        add(searchPanel, constraints);

        String[] columnNames = SharedData.columnNames;
        Object[][] rowData = SharedData.rowData;

        tableModel = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5 || column == 6 || column == 7;
            }
        };

        dataTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        dataTable.setRowSorter(rowSorter);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        add(scrollPane, constraints);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        dataTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = dataTable.getSelectedRow();
                    if (row >= 0) {
                        Object[] rowData = new Object[tableModel.getColumnCount()];
                        for (int i = 0; i < tableModel.getColumnCount(); i++) {
                            rowData[i] = tableModel.getValueAt(row, i);
                        }
                        TaskInfo taskInfo = new TaskInfo(rowData, updatedRowData -> {
                            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                                tableModel.setValueAt(updatedRowData[i], row, i);
                            }
                        });
                        taskInfo.setVisible(true);
                    }
                }
            }
        });
    }

    private void openNewTaskInfo() {
        Object[] emptyRowData = new Object[tableModel.getColumnCount()];
        Arrays.fill(emptyRowData, "");
        TaskInfo taskInfo = new TaskInfo(emptyRowData, updatedRowData -> {
            tableModel.addRow(updatedRowData);
        });
        taskInfo.setVisible(true);
    }
}
