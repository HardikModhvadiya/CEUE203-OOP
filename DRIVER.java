public class DRIVER {
    public static void main(String[] args) {
        String[] testPasswords = {"abc", "password", "Pass123", "Abcd1234!"};
        
        for (String pw : testPasswords) {
            String label = PasswordChecker.strength(pw);
            System.out.println("Final Label: " + label);
            System.out.println("-------------------------");
        }
    }
}
    

