import java.util.Scanner;

class DivideByZeroException extends Exception {
    DivideByZeroException(String message) {
        super(message);
    }
}

public class GuardedCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean success = false;
        int attempt = 0;

        while (!success) {
            attempt++;
            try {
                System.out.print("Enter first number: ");
                String num1Str = sc.nextLine();
                double num1 = Double.parseDouble(num1Str);

                System.out.print("Enter second number: ");
                String num2Str = sc.nextLine();
                double num2 = Double.parseDouble(num2Str);

                System.out.print("Enter operator (+, -, *, /): ");
                String op = sc.nextLine().trim();

                double result;
                switch (op) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        if (num2 == 0) {
                            throw new DivideByZeroException("Cannot divide by zero.");
                        }
                        result = num1 / num2;
                        break;
                    default:
                        System.out.println("Invalid operator. Please use +, -, *, or /.");
                        continue;
                }

                System.out.println("Result: " + result);
                success = true;

            } catch (NumberFormatException e) {
                System.out.println("Input Error: Please enter valid numeric values.");
            } catch (DivideByZeroException e) {
                System.out.println("Math Error: " + e.getMessage());
            } finally {
                System.out.println("[Log] Attempt #" + attempt + " processed.\n");
            }
        }
        sc.close();
    }
}