package loginpage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import dashboard.Dashboard;
import shared.Constants;
import shared.JwtStorage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.prefs.Preferences;

public class LoginPage {

    private static JLabel loginErrorLabel;
    private static JLabel signUpErrorLabel;

    public LoginPage() {
        JFrame frame = new JFrame("Login and Sign Up Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2));

        JPanel loginPanel = createLoginPanel(frame);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel signUpPanel = createSignUpPanel(frame);

        frame.add(loginPanel);
        frame.add(signUpPanel);

        frame.setPreferredSize(new Dimension(1000, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }

    private static JPanel createLoginPanel(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 50, 0, 50);

        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        // Add DocumentListener with a delay to validate email format
        DocumentListener emailDocumentListener = new DocumentListener() {
            private Timer emailTimer;  // Timer to introduce a delay

            {
                emailTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleEmailError();
                    }
                });
                emailTimer.setRepeats(false); // Set to non-repeating
            }

            public void insertUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            private void toggleEmailError() {
                boolean isEmailValid = isValidEmail(emailField.getText().trim());
                if (!isEmailValid && !emailField.getText().trim().isEmpty()) {
                    loginErrorLabel.setText("Invalid email format.");
                    loginErrorLabel.setVisible(true);
                } else {
                    loginErrorLabel.setVisible(false);
                }
                frame.pack();
            }
        };

        // Add DocumentListener to the email field
        emailField.getDocument().addDocumentListener(emailDocumentListener);

        addLabelAndTextField(panel, "Email:", emailField, gbc);
        addLabelAndTextField(panel, "Password:", passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin(emailField.getText(), new String(passwordField.getPassword()), frame));
        panel.add(loginButton, gbc);

        // Create a new error label for login
        loginErrorLabel = new JLabel();
        loginErrorLabel.setForeground(Color.RED);
        loginErrorLabel.setVisible(false);
        gbc.insets = new Insets(0, 50, 10, 50);
        panel.add(loginErrorLabel, gbc);

        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private static void handleLogin(String userEmail, String userPassword, JFrame frame) {
        String jsonInputString = String.format("{\"userEmail\":\"%s\", \"userPassword\":\"%s\"}", userEmail, userPassword);
        try {
        URL url = new URL(Constants.BACKEND_URL+"/api/v1/auth/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
	        try (OutputStream os = conn.getOutputStream()) {
	            byte[] input = jsonInputString.getBytes();
	            os.write(input, 0, input.length);
	        }
	
	        int responseCode = conn.getResponseCode();
	        StringBuilder response = new StringBuilder();
	
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
	                String responseLine;
	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }
	            }
	            String token = extractTokenFromResponse(response.toString());
	            JwtStorage.saveJwtToken(token);
	            SwingUtilities.invokeLater(() -> new Dashboard(userEmail));
	            frame.dispose();
	        } else {
	            // If credentials are wrong or any other connection issue, read the error stream
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
	                String responseLine;
	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }
	            }
	            // Extract error message from response
	            String errorMessage = extractErrorMessageFromResponse(response.toString());
	            loginErrorLabel.setText(errorMessage);
	            loginErrorLabel.setVisible(true);
	        }
	    } catch (IOException ex) {
	        loginErrorLabel.setText("Network error. Please try again later.");
	        loginErrorLabel.setVisible(true);
	    }
    }
    private static String extractErrorMessageFromResponse(String response) {
        String errorPrefix = "\"error\":\"";
        String errorSuffix = "\"";

        int startIndex = response.indexOf(errorPrefix);
        if (startIndex == -1) {
            return "An error occurred, but no error message was provided."; // Error key not found in response
        }
        
        startIndex += errorPrefix.length();
        int endIndex = response.indexOf(errorSuffix, startIndex);
        if (endIndex == -1) {
            return "An error occurred, but no error message was provided."; // No end quote found after error key
        }
        
        return response.substring(startIndex, endIndex);
    }

    private static String extractTokenFromResponse(String response) {
        String tokenRegex = "\"token\":\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(tokenRegex);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null; // Token not found
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
        
        // Add DocumentListener with a delay to validate email format
        DocumentListener emailDocumentListener = new DocumentListener() {
            private Timer emailTimer;  // Timer to introduce a delay

            {
                emailTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleEmailError();
                    }
                });
                emailTimer.setRepeats(false); // Set to non-repeating
            }

            public void insertUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
                emailTimer.restart();
            }

            private void toggleEmailError() {
                boolean isEmailValid = isValidEmail(emailField.getText().trim());
                if (!isEmailValid && !emailField.getText().trim().isEmpty()) {
                    signUpErrorLabel.setText("Invalid email format.");
                    signUpErrorLabel.setVisible(true);
                } else {
                    signUpErrorLabel.setVisible(false);
                }
                frame.pack();
            }
        };

        // Add DocumentListener to the email field
        emailField.getDocument().addDocumentListener(emailDocumentListener);

        addLabelAndTextField(panel, "Email Address:", emailField, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        addLabelAndTextField(panel, "New Password:", passwordField, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        addLabelAndTextField(panel, "Confirm Password:", confirmPasswordField, gbc);

        // Create a new error label for signup
        signUpErrorLabel = new JLabel();
        signUpErrorLabel.setForeground(Color.RED);
        signUpErrorLabel.setVisible(false);
        gbc.insets = new Insets(0, 50, 10, 50);
        panel.add(signUpErrorLabel, gbc);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setEnabled(false);
        panel.add(signUpButton, gbc);

        // Document Listener for all fields
        DocumentListener documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                toggleButton();
            }

            public void removeUpdate(DocumentEvent e) {
                toggleButton();
            }

            public void changedUpdate(DocumentEvent e) {
                toggleButton();
            }

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
                    signUpErrorLabel.setText("Invalid email format.");
                    signUpErrorLabel.setVisible(true);
                } else if (!arePasswordsEqual && confirmPasswordField.getPassword().length > 0) {
                    signUpErrorLabel.setText("Passwords do not match.");
                    signUpErrorLabel.setVisible(true);
                } else {
                    signUpErrorLabel.setVisible(false);
                }

                signUpButton.setEnabled(areAllFieldsFilled && arePasswordsEqual && isEmailValid);
                frame.pack();
            }
        };

        firstNameField.getDocument().addDocumentListener(documentListener);
        lastNameField.getDocument().addDocumentListener(documentListener);
        emailField.getDocument().addDocumentListener(emailDocumentListener);
        passwordField.getDocument().addDocumentListener(documentListener);
        confirmPasswordField.getDocument().addDocumentListener(documentListener);

        signUpButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (password.equals(confirmPassword)) {
                signUpErrorLabel.setVisible(false);
                String json = String.format("{\"userFname\":\"%s\", \"userLname\":\"%s\", \"userEmail\":\"%s\", \"userPassword\":\"%s\"}",
                        firstNameField.getText(), lastNameField.getText(), emailField.getText(), password);
                System.out.println(json);
                try {
                    sendPost(Constants.BACKEND_URL+"/api/v1/auth/register", json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error sending the sign-up request.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                signUpErrorLabel.setVisible(true);
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
        System.out.println(jsonInputString);
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
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Registration Successful. Log in to continue.", "Registered", JOptionPane.INFORMATION_MESSAGE);
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
