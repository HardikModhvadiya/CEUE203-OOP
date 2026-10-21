public class ChatFilter {
    public static void processLogs(String[] logs, String keyword) {
        int matchCount = 0;
        StringBuilder report = new StringBuilder();
        
        String searchWord = keyword.toLowerCase();
        
        for (String log : logs) {
            String[] parts = log.split(" ", 3);
            
            if (parts.length < 3) {
                continue; 
            }
            
            String time = parts[0];
            String user = parts[1];
            String message = parts[2];
            
            if (message.toLowerCase().contains(searchWord)) {
                matchCount++;
                report.append(time).append(" ").append(user).append(": ").append(message).append("\n");
            }
        }
        
        System.out.println("Matches: " + matchCount);
        if (matchCount > 0) {
            System.out.println("Report:\n" + report.toString());
        }
    }
}
