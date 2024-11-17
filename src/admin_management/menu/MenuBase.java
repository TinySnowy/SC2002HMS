package admin_management.menu;

import admin_management.utils.InputValidator;
import admin_management.utils.MenuPrinter;
import java.util.Scanner;

public abstract class MenuBase {
    protected final Scanner scanner;
    protected final MenuPrinter menuPrinter;
    protected final InputValidator inputValidator;

    protected MenuBase() {
        this.scanner = new Scanner(System.in);
        this.menuPrinter = new MenuPrinter();
        this.inputValidator = new InputValidator();
    }

    public abstract void display();
    
    protected abstract boolean handleChoice(int choice);

    protected int getValidChoice(int min, int max) {
        return inputValidator.getValidatedInput(scanner, "Enter choice: ", min, max);
    }

    protected void showError(String message) {
        System.err.println("Error: " + message);
    }
}