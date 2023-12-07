package getsolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetSolution {

    public static void main(String[] args) {
        // The main method can remain for testing the search functionality independently
        // If this is for production, this method may be unnecessary
        JFrame frame = new JFrame("Google Search App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(getSearchActionListener(searchField));

        JPanel panel = new JPanel();
        panel.add(searchField);
        panel.add(searchButton);
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static ActionListener getSearchActionListener(JTextField searchField) {
        return e -> {
            try {
                String searchText = searchField.getText();
                if (!searchText.isEmpty()) {
                    openWebpage("https://www.google.com/search?q=" + URLEncoder.encode(searchText, "UTF-8"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error performing the search.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    public static void openWebpage(String url) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(URI.create(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error opening the web page.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
