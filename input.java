import java.util.Scanner;

public class input {

    public static void main(String[] args){

        Scanner hh = new Scanner(System.in);

        System.out.print("Enter the value of a: ");
int a = hh.nextInt();

        System.out.print("Enter the value of b: ");
        int b= hh.nextInt();
int sum = a+b;
System.out.print("the sum is : " );
        System.out.print(sum);
    }
}