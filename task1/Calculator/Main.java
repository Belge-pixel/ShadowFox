import java.util.*;
import java.io.*;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    public static File help = new File("docs/help.txt");
    public static Scanner helpScanner;

    static {
        try {
            helpScanner = new Scanner(help);
        } catch (FileNotFoundException e) {
            System.err.println("Help file not found: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Converter converter = new Converter();
        String[] options = {"ENHANCED CALCULATOR", "UNITS CONVERTER", "HELP", "CLEAR SCREEN", "EXIT"};
        int choice;

        while (true) {
            printHeader("__NEW ENIAC__");
            printMenu(options);

            System.out.print(">> Choose an option: ");
            choice = getIntInput();

            switch (choice) {
                case 1:
                    clearScreen();
                    calculator.run();
                    break;
                case 2:
                    clearScreen();
                    converter.run();
                    break;
                case 3:
                    clearScreen();
                    showHelp();
                    break;
                case 4:
                    clearScreen();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\033[1;31mInvalid option. Please try again.\033[0m");
                    break;
            }
        }
    }

    // Print header with decoration
    private static void printHeader(String title) {
        System.out.println("\033[1;34m=====================================\033[0m");
        System.out.println("\033[1;32m          " + title + "          \033[0m");
        System.out.println("\033[1;34m=====================================\033[0m");
    }

    // Print menu options
    private static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + "\t" + options[i]);
        }
    }

    // Clear console screen
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Error clearing screen: " + ex.getMessage());
        }
    }

    // Display help file content
    private static void showHelp() {
        if (helpScanner != null) {
            while (helpScanner.hasNextLine()) {
                System.out.println(helpScanner.nextLine());
            }
            try {
                helpScanner = new Scanner(help); // reset scanner for next time
            } catch (FileNotFoundException e) {
                System.err.println("Help file not found: " + e.getMessage());
            }
        } else {
            System.out.println("\033[1;31mHelp file not available.\033[0m");
        }
    }

    // Safely read integer input
    private static int getIntInput() {
        while (true) {
            try {
                int val = scanner.nextInt();
                scanner.nextLine(); // clear buffer
                return val;
            } catch (InputMismatchException e) {
                System.out.print("\033[1;31mInvalid input. Enter a number >> \033[0m");
                scanner.nextLine();
            }
        }
    }
}
