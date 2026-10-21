public class PasswordChecker {
    public static String strength(String pw) {
        int passedRules = 0;
        
        boolean lengthRule = pw.length() >= 8;
        boolean upperRule = pw.matches(".*[A-Z].*");
        boolean digitRule = pw.matches(".*\\d.*");
        boolean specialRule = pw.matches(".*[^a-zA-Z0-9].*");
        
        if (lengthRule) passedRules++;
        if (upperRule) passedRules++;
        if (digitRule) passedRules++;
        if (specialRule) passedRules++;
        
        System.out.println("Testing password: " + pw);
        System.out.println(" - Length >= 8: " + lengthRule);
        System.out.println(" - Contains uppercase: " + upperRule);
        System.out.println(" - Contains digit: " + digitRule);
        System.out.println(" - Contains special character: " + specialRule);
        
        if (passedRules <= 1) {
            return "Weak";
        } else if (passedRules <= 3) {
            return "Medium";
        } else {
            return "Strong";
        }
    }
}
    

