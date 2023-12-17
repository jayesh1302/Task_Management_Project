package services;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import shared.JwtStorage;

import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class newTaskService {

    public static boolean createTask(String taskName, String taskPriority, String taskStatus,
        Date startDate, Date endDate, Date dueDate) {
        String jwtToken = JwtStorage.getJwtToken();
        try {
            URL url = new URL("http://localhost:8080/api/v1/task/create"); 
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); 
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String startDateString = dateFormat.format(startDate);
            String endDateString = endDate == null ? "" : dateFormat.format(endDate);
            String dueDateString = dateFormat.format(dueDate);
            String lastUpdatedString = dateFormat.format(new Date());

            String jsonInputString = String.format(
                "{\"taskName\": \"%s\", \"taskPriority\": \"%s\", \"taskStatus\": \"%s\", " +
                "\"startDate\": \"%s\", \"endDate\": \"%s\", \"dueDate\": \"%s\", \"lastUpdated\": \"%s\", " +
                "\"projectId\": 1, \"assignedTo\": 1, \"assignedBy\": 1}",
                taskName, taskPriority, taskStatus, startDateString, endDateString, dueDateString, lastUpdatedString);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) { // Assuming 201 for successful creation
                System.out.println("Task created successfully.");
                return true;
            } else {
                System.out.println("POST request not worked, response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}