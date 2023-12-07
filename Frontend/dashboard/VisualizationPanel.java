package dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import java.awt.*;

import shared.SharedData;

public class VisualizationPanel extends JPanel {

    public VisualizationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add a tab for all projects
        tabbedPane.add("All Projects", createProjectPanel(0));

        // Add tabs for individual projects
        Map<Integer, String> projectNames = getProjectNamesFromDatabase();
        for (Map.Entry<Integer, String> entry : projectNames.entrySet()) {
            tabbedPane.add(entry.getValue(), createProjectPanel(entry.getKey()));
        }

        add(tabbedPane);
    }

    private JPanel createProjectPanel(int projectId) {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        ChartPanel statusPanel = createChartPanel(createStatusDataset(projectId), "Task Status");
        ChartPanel priorityPanel = createChartPanel(createPriorityDataset(projectId), "Task Priority");
        ChartPanel assignedToPanel = createChartPanel(createAssignedToDataset(projectId), "Assigned To");
        ChartPanel completedByPanel = createChartPanel(createCompletedByDataset(projectId), "Completed By");

        panel.add(statusPanel);
        panel.add(priorityPanel);
        panel.add(assignedToPanel);
        panel.add(completedByPanel);

        return panel;
    }

    private ChartPanel createChartPanel(PieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        // Customize the chart as needed...
        return new ChartPanel(chart);
    }

    private PieDataset createStatusDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Object[] row : SharedData.rowData) {
            if (projectId == 0 || (int) row[0] == projectId) {
                String status = (String) row[3];
                Comparable<?> statusKey = status;
                if (dataset.getIndex(statusKey) >= 0) {
                    dataset.setValue(statusKey, dataset.getValue(statusKey).intValue() + 1);
                } else {
                    dataset.setValue(statusKey, 1);
                }
            }
        }
        return dataset;
    }

    private PieDataset createPriorityDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Object[] row : SharedData.rowData) {
            if (projectId == 0 || (int) row[0] == projectId) {
                String priority = (String) row[6];
                if (dataset.getIndex(priority) >= 0) {
                    dataset.setValue(priority, dataset.getValue(priority).intValue() + 1);
                } else {
                    dataset.setValue(priority, 1);
                }
            }
        }
        return dataset;
    }

    private PieDataset createAssignedToDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> assignedToCount = new HashMap<>();
        for (Object[] row : SharedData.rowData) {
            if (projectId == 0 || (int) row[0] == projectId) {
                String assignedTo = (String) row[7];
                assignedToCount.put(assignedTo, assignedToCount.getOrDefault(assignedTo, 0) + 1);
            }
        }
        assignedToCount.forEach(dataset::setValue);
        return dataset;
    }

    private PieDataset createCompletedByDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> completedByCount = new HashMap<>();
        for (Object[] row : SharedData.rowData) {
            if ((projectId == 0 || (int) row[0] == projectId) && "Resolved".equals(row[3])) {
                String completedBy = (String) row[7];
                completedByCount.put(completedBy, completedByCount.getOrDefault(completedBy, 0) + 1);
            }
        }
        completedByCount.forEach(dataset::setValue);
        return dataset;
    }

    private Map<Integer, String> getProjectNamesFromDatabase() {
        Map<Integer, String> projectNames = new HashMap<>();
        projectNames.put(1, "Project Alpha");
        projectNames.put(2, "Project Beta");
        projectNames.put(3, "Project Gamma");
        projectNames.put(4, "Project Delta");
        projectNames.put(5, "Project Epsilon");
        return projectNames;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new VisualizationPanel());
            frame.setPreferredSize(new Dimension(800, 600));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
