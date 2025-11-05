import java.util.*;

public class Calculator {
    public static Scanner scanner = new Scanner(System.in);

    public static String[] operations = {"+", "-", "*", "/", "sqrt", "log", "exp", "="};

    private String message_error = "", operation = "";
    private CalculatorState state = CalculatorState.STOPPED;
    private Operation currOperation = Operation.NONE;
    private Memory memoryState = Memory.VOID;
    private double currentResult = 0;
    private double input;

    private final String BLUE = "\033[1;34m";
    private final String GREEN = "\033[1;32m";
    private final String RED = "\033[1;31m";
    private final String RESET = "\033[0m";

    private void init() {
        this.currOperation = Operation.NONE;
        this.memoryState = Memory.VOID;
        this.currentResult = 0;
    }

    public void operationShifting(String operation) {
        String op = operation.trim().toLowerCase();
        switch (op) {
            case "+": this.currOperation = Operation.ADDITION; break;
            case "-": this.currOperation = Operation.SUBTRACTION; break;
            case "*": this.currOperation = Operation.MULTIPLICATION; break;
            case "/": this.currOperation = Operation.DIVISION; break;
            case "sqrt": this.currOperation = Operation.SQUARE_ROOT; break;
            case "log": this.currOperation = Operation.LOGARITHM; break;
            case "exp": this.currOperation = Operation.EXPONENTIAL; break;
            case "=": this.currOperation = Operation.EQUAL; break;
            default:
                this.currOperation = Operation.NONE;
                this.message_error = "Unknown operation: " + operation;
                System.out.println(RED + this.message_error + RESET);
        }
    }

    public void performCalculus() {
        if (this.memoryState == Memory.CONTENT) {
            switch (this.currOperation) {
                case ADDITION: this.currentResult += this.input; break;
                case SUBTRACTION: this.currentResult -= this.input; break;
                case MULTIPLICATION: this.currentResult *= this.input; break;
                case DIVISION:
                    if (this.input == 0) {
                        System.out.println(RED + "Error: Division by zero." + RESET);
                        this.state = CalculatorState.ENDED;
                    } else {
                        this.currentResult /= this.input;
                    }
                    break;
                case SQUARE_ROOT:
                    if (this.currentResult < 0) {
                        System.out.println(RED + "Error: Cannot take square root of a negative number." + RESET);
                    } else this.currentResult = Math.sqrt(this.currentResult);
                    break;
                case LOGARITHM:
                    if (this.currentResult <= 0) {
                        System.out.println(RED + "Error: Log undefined for zero or negative numbers." + RESET);
                    } else this.currentResult = Math.log(this.currentResult);
                    break;
                case EXPONENTIAL: this.currentResult = Math.exp(this.currentResult); break;
                case EQUAL:
                    System.out.println(GREEN + "=============================");
                    System.out.println("        Result = " + this.currentResult);
                    System.out.println("=============================" + RESET);
                    this.state = CalculatorState.STOPPED;
                    break;
                default: break;
            }
        }
    }

    public void run() {
        init();
        this.state = CalculatorState.STARTING;
        printHeader("ENHANCED CALCULATOR");

        System.out.print(BLUE + "Enter first number >> " + RESET);
        this.currentResult = getNumber();

        askOperation();

        this.state = CalculatorState.RUNNING;
        this.memoryState = Memory.CONTENT;

        while (this.state == CalculatorState.RUNNING) {
            if (this.currOperation == Operation.EQUAL) {
                performCalculus();
                break;
            }

            if (isUnary(this.currOperation)) {
                performCalculus();
                printCurrentResult();
            } else {
                System.out.print(BLUE + "Enter next number >> " + RESET);
                this.input = getNumber();
                performCalculus();
                if (this.state == CalculatorState.ENDED) break;
                printCurrentResult();
            }

            askOperation();
        }

        System.out.println(GREEN + "Calculator stopped." + RESET);
    }

    private void askOperation() {
        while (true) {
            System.out.print(BLUE + "Enter operation (+, -, *, /, sqrt, log, exp, =) >> " + RESET);
            this.operation = scanner.nextLine().trim().toLowerCase();
            if (!checkOperation(this.operation)) {
                System.out.println(RED + "Invalid operation. Try again." + RESET);
            } else {
                operationShifting(this.operation);
                break;
            }
        }
    }

    private void printHeader(String title) {
        System.out.println(GREEN + "===============================");
        System.out.println("      " + title);
        System.out.println("===============================" + RESET);
    }

    private void printCurrentResult() {
        System.out.println(GREEN + "Current result: " + this.currentResult + RESET);
        System.out.println("-------------------------------");
    }

    private boolean checkOperation(String op) {
        for (String operation : operations) {
            if (operation.equalsIgnoreCase(op)) return true;
        }
        return false;
    }

    private boolean isUnary(Operation op) {
        return (op == Operation.SQUARE_ROOT || op == Operation.LOGARITHM || op == Operation.EXPONENTIAL);
    }

    private double getNumber() {
        while (true) {
            try {
                double val = scanner.nextDouble();
                scanner.nextLine();
                return val;
            } catch (Exception e) {
                System.out.println(RED + "Invalid number. Please try again." + RESET);
                System.out.print(BLUE + "Enter number >> " + RESET);
                scanner.nextLine();
            }
        }
    }
}
