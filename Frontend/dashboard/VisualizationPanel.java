//package dashboard;
//
//import shared.SharedData;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PiePlot;
//import org.jfree.data.general.DefaultPieDataset;
//import org.jfree.data.general.PieDataset;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class VisualizationPanel extends JPanel {
//
//    public VisualizationPanel() {
//        setLayout(new BorderLayout());
//        JTabbedPane tabbedPane = new JTabbedPane();
//
//        // Fetch project names from SharedData
//        Map<Integer, String> projectNames = getProjectNamesFromDatabase();
//        for (Map.Entry<Integer, String> entry : projectNames.entrySet()) {
//            tabbedPane.add(entry.getValue(), createProjectPanel(entry.getKey()));
//        }
//
//        add(tabbedPane);
//    }
//
//    private JPanel createProjectPanel(int projectId) {
//        JPanel panel = new JPanel(new GridLayout(2, 2));
//
//        ChartPanel statusPanel = createChartPanel(createStatusDataset(projectId), "Task Status");
//        ChartPanel priorityPanel = createChartPanel(createPriorityDataset(projectId), "Task Priority");
//        ChartPanel assignedToPanel = createChartPanel(createAssignedToDataset(projectId), "Assigned To");
//        ChartPanel requestedByPanel = createChartPanel(createRequestedByDataset(projectId), "Completed By");
//
//        panel.add(statusPanel);
//        panel.add(priorityPanel);
//        panel.add(assignedToPanel);
//        panel.add(requestedByPanel);
//
//        return panel;
//    }
//
//    private ChartPanel createChartPanel(PieDataset dataset, String title) {
//        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
//        PiePlot plot = (PiePlot) chart.getPlot();
//        // Customize the chart as needed...
//        return new ChartPanel(chart);
//    }
//
//    private PieDataset createStatusDataset(int projectId) {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
//        for (Object[] row : taskDetails) {
//            String status = (String) row[6];
//            Comparable<?> statusKey = status;
//            if (dataset.getIndex(statusKey) >= 0) {
//                dataset.setValue(statusKey, dataset.getValue(statusKey).intValue() + 1);
//            } else {
//                dataset.setValue(statusKey, 1);
//            }
//        }
//        return dataset;
//    }
//
//    private PieDataset createPriorityDataset(int projectId) {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
//        for (Object[] row : taskDetails) {
//            String priority = (String) row[9];
//            if (dataset.getIndex(priority) >= 0) {
//                dataset.setValue(priority, dataset.getValue(priority).intValue() + 1);
//            } else {
//                dataset.setValue(priority, 1);
//            }
//        }
//        return dataset;
//    }
//
//    private PieDataset createAssignedToDataset(int projectId) {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        Map<String, Integer> assignedToCount = new HashMap<>();
//        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
//        for (Object[] row : taskDetails) {
//            String assignedTo = (String) row[10];
//            assignedToCount.put(assignedTo, assignedToCount.getOrDefault(assignedTo, 0) + 1);
//        }
//        assignedToCount.forEach(dataset::setValue);
//        return dataset;
//    }
//
//    private PieDataset createRequestedByDataset(int projectId) {
//    	DefaultPieDataset dataset = new DefaultPieDataset();
//        Map<String, Integer> requestedByCount = new HashMap<>();
//        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
//        for (Object[] row : taskDetails) {
//            String requestedBy = (String) row[11];
//            requestedByCount.put(requestedBy, requestedByCount.getOrDefault(requestedBy, 0) + 1);
//        }
//        requestedByCount.forEach(dataset::setValue);
//        return dataset;
//    }
//
//    private Map<Integer, String> getProjectNamesFromDatabase() {
//        Map<Integer, String> projectNames = new HashMap<>();
//        Object[][] allProjects = SharedData.getAllProjectDetails();
//        for (Object[] project : allProjects) {
//            Integer projectId = (Integer) project[0];
//            String projectName = (String) project[1];
//            projectNames.put(projectId, projectName);
//        }
//        return projectNames;
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Dashboard");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.getContentPane().add(new VisualizationPanel());
//            frame.setPreferredSize(new Dimension(800, 600));
//            frame.pack();
//            frame.setVisible(true);
//        });
//    }
//}
package dashboard;

import shared.SharedData;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VisualizationPanel extends JPanel {

    private JTabbedPane tabbedPane;

    public VisualizationPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Fetch project names from SharedData
        refreshVisualization();

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");

        refreshButton.setPreferredSize(new Dimension(90, 22)); 
        buttonPanel.add(refreshButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        

        add(tabbedPane);
    }

    private JPanel createProjectPanel(int projectId) {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        ChartPanel statusPanel = createChartPanel(createStatusDataset(projectId), "Task Status");
        ChartPanel priorityPanel = createChartPanel(createPriorityDataset(projectId), "Task Priority");
        ChartPanel assignedToPanel = createChartPanel(createAssignedToDataset(projectId), "Assigned To");
        ChartPanel requestedByPanel = createChartPanel(createRequestedByDataset(projectId), "Completed By");

        panel.add(statusPanel);
        panel.add(priorityPanel);
        panel.add(assignedToPanel);
        panel.add(requestedByPanel);

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
        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
        for (Object[] row : taskDetails) {
            String status = (String) row[6];
            Comparable<?> statusKey = status;
            if (dataset.getIndex(statusKey) >= 0) {
                dataset.setValue(statusKey, dataset.getValue(statusKey).intValue() + 1);
            } else {
                dataset.setValue(statusKey, 1);
            }
        }
        return dataset;
    }

    private PieDataset createPriorityDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
        for (Object[] row : taskDetails) {
            String priority = (String) row[9];
            if (dataset.getIndex(priority) >= 0) {
                dataset.setValue(priority, dataset.getValue(priority).intValue() + 1);
            } else {
                dataset.setValue(priority, 1);
            }
        }
        return dataset;
    }

    private PieDataset createAssignedToDataset(int projectId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> assignedToCount = new HashMap<>();
        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
        for (Object[] row : taskDetails) {
            String assignedTo = (String) row[10];
            assignedToCount.put(assignedTo, assignedToCount.getOrDefault(assignedTo, 0) + 1);
        }
        assignedToCount.forEach(dataset::setValue);
        return dataset;
    }

    private PieDataset createRequestedByDataset(int projectId) {
    	DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> requestedByCount = new HashMap<>();
        Object[][] taskDetails = SharedData.getTasksByProjectId(projectId);
        for (Object[] row : taskDetails) {
            String requestedBy = (String) row[11];
            requestedByCount.put(requestedBy, requestedByCount.getOrDefault(requestedBy, 0) + 1);
        }
        requestedByCount.forEach(dataset::setValue);
        return dataset;
    }

    private Map<Integer, String> getProjectNamesFromDatabase() {
        Map<Integer, String> projectNames = new HashMap<>();
        Object[][] allProjects = SharedData.getAllProjectDetails();
        for (Object[] project : allProjects) {
            Integer projectId = (Integer) project[0];
            String projectName = (String) project[1];
            projectNames.put(projectId, projectName);
        }
        return projectNames;
    }

    private void refreshVisualization() {
        // Remove all tabs and recreate them
        tabbedPane.removeAll();
        Map<Integer, String> projectNames = getProjectNamesFromDatabase();
        for (Map.Entry<Integer, String> entry : projectNames.entrySet()) {
            tabbedPane.add(entry.getValue(), createProjectPanel(entry.getKey()));
        }

        // Repaint the panel
        revalidate();
        repaint();
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
