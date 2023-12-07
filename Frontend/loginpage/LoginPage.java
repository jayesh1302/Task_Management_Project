package loginpage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LoginPage {

    private static JLabel errorLabel; // Error message label

    public static void main(String[] args) {
        errorLabel = new JLabel("Passwords do not match.");
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        JFrame frame = new JFrame("Login and Sign Up Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2));

        JPanel loginPanel = createLoginPanel();
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel signUpPanel = createSignUpPanel(frame);

        frame.add(loginPanel);
        frame.add(signUpPanel);

        frame.setPreferredSize(new Dimension(1000, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 50, 0, 50);

        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        addLabelAndTextField(panel, "Username:", new JTextField(20), gbc);
        panel.add(Box.createVerticalStrut(10), gbc);

        addLabelAndTextField(panel, "Password:", new JPasswordField(20), gbc);

        JButton loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }
    private static JPanel createSignUpPanel(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 50, 0, 50);

        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        JTextField firstNameField = new JTextField(20);
        addLabelAndTextField(panel, "First Name:", firstNameField, gbc);

        JTextField lastNameField = new JTextField(20);
        addLabelAndTextField(panel, "Last Name:", lastNameField, gbc);

        JTextField emailField = new JTextField(20);
        addLabelAndTextField(panel, "Email Address:", emailField, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        addLabelAndTextField(panel, "New Password:", passwordField, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        addLabelAndTextField(panel, "Confirm Password:", confirmPasswordField, gbc);

        // Error Label
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        gbc.insets = new Insets(0, 50, 10, 50);
        panel.add(errorLabel, gbc);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setEnabled(false); // Initially disabled
        panel.add(signUpButton, gbc);

        // Document Listener for all fields
        DocumentListener documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { toggleButton(); }
            public void removeUpdate(DocumentEvent e) { toggleButton(); }
            public void changedUpdate(DocumentEvent e) { toggleButton(); }

            private void toggleButton() {
                boolean areAllFieldsFilled = !firstNameField.getText().trim().isEmpty()
                        && !lastNameField.getText().trim().isEmpty()
                        && !emailField.getText().trim().isEmpty()
                        && passwordField.getPassword().length > 0
                        && confirmPasswordField.getPassword().length > 0;
                boolean arePasswordsEqual = new String(passwordField.getPassword())
                        .equals(new String(confirmPasswordField.getPassword()));
                boolean isEmailValid = isValidEmail(emailField.getText().trim());

                if (!isEmailValid && !emailField.getText().trim().isEmpty()) {
                    errorLabel.setText("Invalid email format.");
                    errorLabel.setVisible(true);
                } else if (!arePasswordsEqual && confirmPasswordField.getPassword().length > 0) {
                    errorLabel.setText("Passwords do not match.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setVisible(false);
                }

                signUpButton.setEnabled(areAllFieldsFilled && arePasswordsEqual && isEmailValid);
                frame.pack();
            }
        };

        firstNameField.getDocument().addDocumentListener(documentListener);
        lastNameField.getDocument().addDocumentListener(documentListener);
        emailField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);
        confirmPasswordField.getDocument().addDocumentListener(documentListener);

        signUpButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (password.equals(confirmPassword)) {
                errorLabel.setVisible(false);
                String json = String.format("{\"userFname\":\"%s\", \"userLname\":\"%s\", \"userEmail\":\"%s\", \"userPwd\":\"%s\"}",
                        firstNameField.getText(), lastNameField.getText(), emailField.getText(), password);

                try {
                    sendPost("http://10.18.161.209:8080/api/v1/users/add", json);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error sending the sign up request.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                errorLabel.setVisible(true);
                frame.pack(); // Repack the frame to accommodate the error label
            }
        });

        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) return false;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



    private static void sendPost(String targetUrl, String jsonInputString) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        } finally {
            conn.disconnect();
        }
    }

    private static void addLabelAndTextField(JPanel panel, String labelText, JTextField textField, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        gbc.insets = new Insets(2, 50, 0, 50);
        panel.add(label, gbc);

        gbc.insets = new Insets(0, 50, 10, 50);
        textField.setPreferredSize(new Dimension(200, 24));
        panel.add(textField, gbc);
    }
}
