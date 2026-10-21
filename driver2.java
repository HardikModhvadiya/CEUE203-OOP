
import java.util.Scanner;

public class driver2 {
    public static void main(String[] args) {
        String[] logs = {
            "10:05 alice Hello there",
            "10:07 bob Hi alice",
            "10:08 system_error", 
            "10:10 charlie I just wanted to say hello to everyone"
        };
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a keyword to search for: ");
        String keyword = scanner.nextLine();
        
        System.out.println("\n--- Search Results ---");
        ChatFilter.processLogs(logs, keyword);
        
        scanner.close();
    }
}