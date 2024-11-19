package admin_management.menu;

import admin_management.utils.InputValidator;
import admin_management.utils.MenuPrinter;
import java.util.Scanner;

/**
 * Abstract base class for all menu interfaces in the hospital management system.
 * Provides common functionality for menu display, input validation, and error handling.
 * Serves as a template for specific menu implementations.
 */
public abstract class MenuBase {
    /** Scanner for reading user input across all menus */
    protected final Scanner scanner;
    
    /** Utility for consistent menu printing across the system */
    protected final MenuPrinter menuPrinter;
    
    /** Validator for ensuring valid user input */
    protected final InputValidator inputValidator;

    /**
     * Constructs a MenuBase with common components.
     * Initializes the scanner, menu printer, and input validator
     * that will be used by all derived menu classes.
     */
    protected MenuBase() {
        this.scanner = new Scanner(System.in);
        this.menuPrinter = new MenuPrinter();
        this.inputValidator = new InputValidator();
    }

    /**
     * Abstract method to display the menu interface.
     * Must be implemented by each specific menu class to define
     * its unique display format and options.
     */
    public abstract void display();
    
    /**
     * Abstract method to handle user menu choices.
     * Must be implemented by each specific menu class to define
     * its unique choice handling logic.
     * 
     * @param choice The user's menu selection
     * @return true to continue displaying menu, false to exit
     */
    protected abstract boolean handleChoice(int choice);

    /**
     * Gets and validates user input within a specified range.
     * Ensures that the user's input is both numeric and within
     * the valid range of menu options.
     * 
     * @param min Minimum valid choice value (inclusive)
     * @param max Maximum valid choice value (inclusive)
     * @return The validated user input
     * @throws IllegalArgumentException if input is invalid
     */
    protected int getValidChoice(int min, int max) {
        return inputValidator.getValidatedInput(scanner, "Enter choice: ", min, max);
    }

    /**
     * Displays an error message to the user in a consistent format.
     * Prefixes all error messages with "Error: " for clear identification.
     * 
     * @param message The error message to display
     */
    protected void showError(String message) {
        System.err.println("Error: " + message);
    }

    /**
     * Utility method to pause execution until user presses Enter.
     * Provides better user experience by controlling information flow
     * and allowing users to read output before continuing.
     */
    protected void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clears the console screen for better readability.
     * Note: Implementation may vary based on the operating system.
     */
    protected void clearScreen() {
        // Implementation varies by OS
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback to printing newlines if clear screen fails
            System.out.println("\n".repeat(50));
        }
    }

    /**
     * Validates that a string input is not empty or just whitespace.
     * 
     * @param input The string to validate
     * @param fieldName The name of the field being validated (for error messages)
     * @throws IllegalArgumentException if input is empty or just whitespace
     */
    protected void validateStringInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    /**
     * Formats a title string with consistent decoration.
     * Creates a visually appealing header for menu sections.
     * 
     * @param title The title to format
     * @return The formatted title string
     */
    protected String formatTitle(String title) {
        String decoration = "=".repeat(50);
        return String.format("\n%s\n%s\n%s\n", decoration, title, decoration);
    }
}