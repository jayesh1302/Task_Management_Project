package dashboard;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import dashboard.VisualizationPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import loginpage.LoginPage;
import shared.JwtStorage;

public class Dashboard extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);
    private JList<String> menuList;

    public Dashboard(String userEmail) {
    	String dashEmail = userEmail;
        setTitle("Dashboard - "+dashEmail);
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.RED); 
        logoutButton.setBackground(Color.WHITE); 
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));

        logoutButton.addActionListener((event) -> {
            JwtStorage.clearJwtToken();
            dispose();
            SwingUtilities.invokeLater(LoginPage::new);
        });

        // Assuming there's a panel or container for the logoutButton at the bottom left
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(logoutButton, BorderLayout.WEST);

        // Add the bottomPanel to the JFrame
        add(bottomPanel, BorderLayout.SOUTH);

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
    	contentPanel.add(new ProjectPanel(), "Projects");
        contentPanel.add(new VisualizationPanel(), "Visualizations");
//        contentPanel.add(new DashboardPanel(), "All");
        // Add the other panels here, similar to the above
        // contentPanel.add(new LastWeekPanel(), "Last week");
        // contentPanel.add(new LastMonthPanel(), "Last month");
        // contentPanel.add(new LastYearPanel(), "Last year");
    }

    private static class MenuListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
        	Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
            if (isSelected) {
                label.setBackground(new Color(173, 216, 230));
                if (value.equals("Last week") || value.equals("Last month") || value.equals("Last year")) {
                	c.setForeground(Color.GRAY); // Set text color to grey for specific items
                }
            } else {
                label.setBackground(Color.WHITE);
                if (value.equals("Last week") || value.equals("Last month") || value.equals("Last year")) {
                	c.setForeground(Color.GRAY); // Set text color to grey for specific items
                }
            }
            return label;
        }
       
    }
}
