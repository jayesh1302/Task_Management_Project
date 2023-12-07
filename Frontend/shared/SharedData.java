package shared;
import java.util.ArrayList;

public class SharedData {
    public static final String[] columnNames = {
        "PROJECT_ID", "TASK_ID", "UPDATED", "STATUS", "TITLE", "COMMENTS", "PRIORITY", "ASSIGNED TO", "REQUESTER"
    };

    public static final Object[][] rowData = {
            {1, 1, "11/01/2023 11:00 AM", "Resolved", "Unable to log into Salesforce", "Understood", "HIGH", "Ethan Noah", "Allison"},
            {1, 2, "11/02/2023 1:00 PM", "In Progress", "Need help with printer", "Which printer?", "MEDIUM", "Technical Support", "Reah Stanley"},
            {1, 3, "11/06/2023 2:00 PM", "Escalated", "Outlook not loading new emails", "Understood", "HIGH", "Jonas Besson", "Zach Smith"},
            {1, 4, "11/07/2023 9:30 AM", "Unresolved", "Software installation issue", "Needs software reinstall", "MEDIUM", "IT Department", "Paul Taylor"},
            {1, 5, "11/08/2023 3:45 PM", "Resolved", "Network connectivity problem", "Restarted router", "LOW", "Network Team", "Laura Clark"},
            {1, 6, "11/09/2023 2:15 PM", "In Progress", "Email not syncing on mobile", "Checking email settings", "HIGH", "Mobile Support", "Tom Johnson"},
            {1, 7, "11/10/2023 8:30 AM", "Escalated", "Password reset request", "Waiting for user confirmation", "MEDIUM", "IT Helpdesk", "Sophia Parker"},
            {1, 8, "11/11/2023 4:00 PM", "Unresolved", "Software bug report", "Investigating the issue", "HIGH", "Development Team", "Daniel Adams"},
            {1, 9, "11/12/2023 1:45 PM", "Resolved", "Server maintenance", "Completed successfully", "LOW", "IT Operations", "Olivia White"},
            {1, 10, "11/13/2023 10:15 AM", "In Progress", "Website update request", "Reviewing content changes", "MEDIUM", "Web Team", "Liam Jackson"},
            {1, 11, "11/14/2023 3:20 PM", "Escalated", "VPN connectivity problem", "Checking server status", "HIGH", "Network Team", "Ava Green"},
            {1, 12, "11/15/2023 9:00 AM", "Unresolved", "Printer not working", "Need to replace toner", "LOW", "IT Helpdesk", "Sophie White"},
            {1, 13, "11/16/2023 2:30 PM", "Resolved", "Software update request", "Updated successfully", "MEDIUM", "Development Team", "James Anderson"},
            {1, 14, "11/17/2023 11:45 AM", "In Progress", "Network outage", "Checking network infrastructure", "HIGH", "Network Team", "Aiden Smith"},
            {1, 15, "11/18/2023 3:00 PM", "Unresolved", "Email not sending", "Investigating SMTP settings", "MEDIUM", "Email Support", "Isabella Lee"},
            {1, 16, "11/19/2023 10:30 AM", "Resolved", "Account access issue", "Password reset requested", "MEDIUM", "IT Helpdesk", "Sophie White"},
            {1, 17, "11/20/2023 4:15 PM", "Unresolved", "Website downtime", "Server rebooting", "HIGH", "Web Team", "David Martin"},
            {2, 18, "11/21/2023 2:20 PM", "Escalated", "Server performance issue", "Monitoring server resources", "HIGH", "IT Operations", "Emma Davis"},
            {2, 19, "11/22/2023 8:45 AM", "Resolved", "Software licensing inquiry", "Provided license details", "MEDIUM", "Sales Team", "Henry Brown"},
            {2, 20, "11/23/2023 12:00 PM", "In Progress", "Computer not booting", "Troubleshooting hardware", "HIGH", "IT Helpdesk", "Sophie White"},
            {2, 21, "11/24/2023 3:50 PM", "Escalated", "Email attachment issue", "Checking mail server settings", "MEDIUM", "Email Support", "Sophia King"},
            {2, 22, "11/25/2023 9:15 AM", "Unresolved", "File access problem", "Permission issues", "MEDIUM", "IT Helpdesk", "Sophie White"},
            {2, 23, "11/26/2023 1:20 PM", "Resolved", "Database connection error", "Restarted the database", "LOW", "Database Team", "Lucas Scott"},
            {3, 24, "11/27/2023 2:35 PM", "In Progress", "Server backup request", "Scheduling backup job", "MEDIUM", "IT Operations", "Ella Wilson"},
            {3, 25, "11/28/2023 10:10 AM", "Unresolved", "Phone not working", "Checking phone settings", "MEDIUM", "Phone Support", "Mia Turner"},
            {3, 26, "11/29/2023 3:15 PM", "Resolved", "Access rights issue", "Updated access permissions", "LOW", "IT Helpdesk", "Sophie White"},
            {3, 27, "11/15/2023 9:00 AM", "Unresolved", "Printer not working", "Need to replace toner", "LOW", "IT Helpdesk", "Sophie White"},
            {3, 28, "11/16/2023 2:30 PM", "Resolved", "Software update request", "Updated successfully", "MEDIUM", "Development Team", "James Anderson"},
            {3, 29, "11/17/2023 11:45 AM", "In Progress", "Network outage", "Checking network infrastructure", "HIGH", "Network Team", "Aiden Smith"},
            {4, 30, "11/18/2023 3:00 PM", "Unresolved", "Email not sending", "Investigating SMTP settings", "MEDIUM", "Email Support", "Isabella Lee"},
            {4, 31, "11/19/2023 10:30 AM", "Resolved", "Account access issue", "Password reset requested", "MEDIUM", "IT Helpdesk", "Sophie White"},
            {4, 32, "11/20/2023 4:15 PM", "Unresolved", "Website downtime", "Server rebooting", "HIGH", "Web Team", "David Martin"},
            {5, 33, "11/21/2023 2:20 PM", "Escalated", "Server performance issue", "Monitoring server resources", "HIGH", "IT Operations", "Emma Dav4, is"},
            {5, 34, "11/22/2023 8:45 AM", "Resolved", "Software licensing inquiry", "Provided license details", "MEDIUM", "Sales Team", "Henry Brow5, n"}
    }; 
    public static Object[][] getTasksByProjectId(int projectId) {
        ArrayList<Object[]> filteredTasks = new ArrayList<>();
        for (Object[] row : rowData) {
            if ((int) row[0] == projectId) {
                filteredTasks.add(row);
            }
        }
        return filteredTasks.toArray(new Object[0][]);
    }
}