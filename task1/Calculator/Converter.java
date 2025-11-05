import java.util.*;

public class Converter {
    public static Scanner scanner = new Scanner(System.in);
    public static String services[] = {"TEMPERATURE", "CURRENCY", "DISTANCE"};
    private int choice;

    // Display conversion services with styling
    public static void showServices() {
        System.out.println("\033[1;34m================ Conversion Services ================\033[0m");
        for (int i = 0; i < services.length; i++) {
            System.out.println((i + 1) + "\t" + services[i]);
        }
        System.out.println("\033[1;34m===================================================\033[0m");
    }

    // Main run method
    public void run() {
        clearScreen();
        showServices();
        System.out.println("\033[1;32mUnits Converter is running...\033[0m");
        System.out.println("Choose a service by entering the corresponding number:");
        this.choice = getChoice();
        convert();
    }

    // Safely read integer input for choices
    public int getChoice() {
        while (true) {
            try {
                int val = scanner.nextInt();
                scanner.nextLine(); // clear buffer
                return val;
            } catch (InputMismatchException e) {
                System.out.print("\033[1;31mInvalid choice. Enter a number >> \033[0m");
                scanner.nextLine(); // clear buffer
            }
        }
    }

    // Conversion dispatcher
    public void convert() {
        switch (this.choice) {
            case 1:
                temperatureMenu();
                break;
            case 2:
                currencyMenu();
                break;
            case 3:
                distanceMenu();
                break;
            default:
                System.out.println("\033[1;31mInvalid service choice.\033[0m");
                break;
        }
    }

    private void temperatureMenu() {
        System.out.println("\033[1;33mTemperature Conversion Selected.\033[0m");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.print("Enter your choice >> ");
        int tempChoice = getChoice();
        convertTemperature(tempChoice);
    }

    private void currencyMenu() {
        System.out.println("\033[1;33mCurrency Conversion Selected.\033[0m");
        System.out.println("1. USD to EUR");
        System.out.println("2. EUR to USD");
        System.out.print("Enter your choice >> ");
        int currChoice = getChoice();
        convertCurrency(currChoice);
    }

    private void distanceMenu() {
        System.out.println("\033[1;33mDistance Conversion Selected.\033[0m");
        System.out.println("1. Kilometers to Miles");
        System.out.println("2. Miles to Kilometers");
        System.out.print("Enter your choice >> ");
        int distChoice = getChoice();
        convertDistance(distChoice);
    }

    // Safely read a double value
    private double getDoubleInput() {
        while (true) {
            try {
                double input = scanner.nextDouble();
                scanner.nextLine(); // clear buffer
                return input;
            } catch (InputMismatchException e) {
                System.out.print("\033[1;31mInvalid number. Enter again >> \033[0m");
                scanner.nextLine();
            }
        }
    }

    // Temperature conversions
    private void convertTemperature(int choice) {
        if (choice == 1) {
            System.out.print("Enter temperature in Celsius >> ");
            double celsius = getDoubleInput();
            double fahrenheit = (celsius * 9 / 5) + 32;
            System.out.println(celsius + " °C = " + fahrenheit + " °F");
        } else if (choice == 2) {
            System.out.print("Enter temperature in Fahrenheit >> ");
            double fahrenheit = getDoubleInput();
            double celsius = (fahrenheit - 32) * 5 / 9;
            System.out.println(fahrenheit + " °F = " + celsius + " °C");
        } else {
            System.out.println("\033[1;31mInvalid temperature conversion choice.\033[0m");
        }
    }

    // Distance conversions
    private void convertDistance(int choice) {
        if (choice == 1) {
            System.out.print("Enter distance in Kilometers >> ");
            double km = getDoubleInput();
            double miles = km * 0.621371;
            System.out.println(km + " km = " + miles + " miles");
        } else if (choice == 2) {
            System.out.print("Enter distance in Miles >> ");
            double miles = getDoubleInput();
            double km = miles / 0.621371;
            System.out.println(miles + " miles = " + km + " km");
        } else {
            System.out.println("\033[1;31mInvalid distance conversion choice.\033[0m");
        }
    }

    // Currency conversions
    private void convertCurrency(int choice) {
        if (choice == 1) {
            System.out.print("Enter amount in USD >> ");
            double usd = getDoubleInput();
            double eur = usd * 0.85; // example rate
            System.out.println(usd + " USD = " + eur + " EUR");
        } else if (choice == 2) {
            System.out.print("Enter amount in EUR >> ");
            double eur = getDoubleInput();
            double usd = eur / 0.85;
            System.out.println(eur + " EUR = " + usd + " USD");
        } else {
            System.out.println("\033[1;31mInvalid currency conversion choice.\033[0m");
        }
    }

    // Clear console screen
    private void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Error clearing screen: " + e.getMessage());
        }
    }
}
