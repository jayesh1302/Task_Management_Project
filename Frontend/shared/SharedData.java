package shared;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;


public class SharedData {
    // Updated column names as per your request
    public static final String[] columnNames = {
        "PROJECT_ID", "TASK_ID", "START_DATE", "END_DATE", "DUE_DATE", "LAST_UPDATE", "STATUS", "TITLE", "COMMENTS", "PRIORITY", "ASSIGNED TO", "REQUESTER"
    };

    public static Object[][] rowData;

    static {
        rowData = getTasksByProjectId(1); // Initializing rowData with data from the API
    }

    public static Object[][] getAllProjectDetails() {
        String urlString = Constants.BACKEND_URL + "/api/v1/project/allProjects";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXRpa3ZpZzIyQGdtYWlsLmNvbSIsInVzZXJJZCI6NSwiaWF0IjoxNzAyMjM5Mzc1LCJleHAiOjE3MDIzMjU3NzV9.bqfDhrfV1oKaMG2cvok-tSuW4JwHOBz57m3Ap0TvLI0");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray projects = jsonResponse.getJSONArray("data");

            Object[][] projectDetails = new Object[projects.length()][];
            for (int i = 0; i < projects.length(); i++) {
                JSONObject project = projects.getJSONObject(i);
                int projectId = project.getInt("projectId");
                String projectName = project.getString("projectName");
                String startDate = project.getString("startDate").substring(0, 10); // Assuming you need date part only
                String completionDate = project.isNull("completionDate") ? "" : project.getString("completionDate").substring(0, 10);

                projectDetails[i] = new Object[]{projectId, projectName, startDate, completionDate};
            }

            return projectDetails;

        } catch (Exception e) {
            e.printStackTrace();
            return new Object[][]{}; // Return an empty array in case of an error
        }
    }

    public static Object[][] getTasksByProjectId(int projectId) {
        String urlString = Constants.BACKEND_URL + "/api/v1/task/getByProjectId/" + projectId;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXRpa3ZpZzIyQGdtYWlsLmNvbSIsInVzZXJJZCI6NSwiaWF0IjoxNzAyMjM5Mzc1LCJleHAiOjE3MDIzMjU3NzV9.bqfDhrfV1oKaMG2cvok-tSuW4JwHOBz57m3Ap0TvLI0");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray tasks = jsonResponse.getJSONArray("data");

            Object[][] taskDetails = new Object[tasks.length()][];
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                int taskId = task.getInt("taskId");
                String taskName = task.getString("taskName");
                String taskPriority = task.getString("taskPriority");
                String taskStatus = task.getString("taskStatus");
                String startDate = task.getString("startDate").substring(0, 10); // Assuming you need date part only
                String endDate = task.optString("endDate").isEmpty() ? "" : task.getString("endDate").substring(0, 10);
                String dueDate = task.getString("dueDate").substring(0, 10);
                String lastUpdated = task.getString("lastUpdated").substring(0, 10);
                String projectName = task.getString("projectName");
                String assignedTo = task.getString("assignedTo");
                String requester = task.getString("requester");

                taskDetails[i] = new Object[]{projectId, taskId, startDate, endDate, dueDate, lastUpdated, taskStatus, taskName, "", taskPriority, assignedTo, requester};
            }

            return taskDetails;

        } catch (Exception e) {
            e.printStackTrace();
            return new Object[][]{}; // Return an empty array in case of an error
        }
    }

}
