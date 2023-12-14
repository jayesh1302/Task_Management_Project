package services;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import shared.JwtStorage;

import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateTaskService {

    public static void updateTask(int taskId, String taskName, String taskPriority, String taskStatus, 
                                  Date startDate, Date endDate, Date dueDate) {
    	String jwtToken = JwtStorage.getJwtToken();
        try {
        	
            URL url = new URL("http://localhost:8080/api/v1/task/update/" + taskId);
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd");
            String startDateString = dateFormat.format(startDate);

            String endDateString = endDate == null ? "" : dateFormat.format(endDate);
            String dueDateString = dateFormat.format(dueDate);

            String jsonInputString = String.format("{\"taskName\": \"%s\", \"taskPriority\": \"%s\", \"taskStatus\": \"%s\", "
                    + "\"startDate\": \"%s\", \"endDate\": \"%s\", \"dueDate\": \"%s\", "
                    + "\"projectId\": 1, \"assignedTo\": 2, \"assignedBy\": 1}", 
                    taskName, taskPriority, taskStatus, startDateString.toString(), endDateString.toString(), dueDateString.toString());
            System.out.println("***************************");
            System.out.println(jsonInputString);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("PUT Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Task updated successfully.");
            } else {
                System.out.println("PUT request not worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
