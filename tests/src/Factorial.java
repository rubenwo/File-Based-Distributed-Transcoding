import java.util.Scanner;

public class Factorial {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("Please enter a number.");
            double num = scanner.nextDouble();
            double factorial = calculateFactorial(num);
            System.out.println(factorial);
            System.out.println("Factorial of " + num + " = " + factorial);
        }
    }

    public static double calculateFactorial(double num) {
        double factorial = num;
        while (num > 1) {
            factorial *= (num - 1);
            num -= 1;
        }
        return factorial;
    }
}
