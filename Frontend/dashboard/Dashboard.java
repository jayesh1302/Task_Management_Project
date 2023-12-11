package dashboard;
import javax.swing.*;
import dashboard.VisualizationPanel;
import dashboard.DashboardPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);
    private JList<String> menuList;

    public Dashboard() {
        setTitle("Dashboard");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] menuItems = {"Projects", "Visualizations", "All", "Last week", "Last month", "Last year"};
        menuList = new JList<>(menuItems);
        menuList.setCellRenderer(new MenuListCellRenderer());
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setSelectedIndex(0);

        menuList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(contentPanel, menuList.getSelectedValue());
            }
        });

        menuList.setBackground(new Color(245, 245, 245));
        menuList.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane listScroller = new JScrollPane(menuList);
        listScroller.setPreferredSize(new Dimension(200, 800));
        listScroller.setBorder(BorderFactory.createEmptyBorder());

        setupCards();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroller, contentPanel);
        splitPane.setDividerLocation(200);
        getContentPane().add(splitPane);

        setVisible(true);
    }

    private void setupCards() {
        // Assuming each panel class is defined in its own file and they are all public
    	contentPanel.add(new ProjectPanel(), "Projects");
        contentPanel.add(new VisualizationPanel(), "Visualizations");
        contentPanel.add(new DashboardPanel(), "All");
        // Add the other panels here, similar to the above
        // contentPanel.add(new LastWeekPanel(), "Last week");
        // contentPanel.add(new LastMonthPanel(), "Last month");
        // contentPanel.add(new LastYearPanel(), "Last year");
    }

    private static class MenuListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
            if (isSelected) {
                label.setBackground(new Color(173, 216, 230));
            } else {
                label.setBackground(Color.WHITE);
            }
            return label;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(Dashboard::new);
//    }
}
