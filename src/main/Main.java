package main;

import user_management.*;

public class Main {
    public static void main(String[] args) {
        UserController userController = new UserController();
        userController.initializeUsers();

        User user = userController.getUserById("P001");
        if (user != null) {
            System.out.println("User Role: " + user.getRole());
            user.displayMenu();
        } else {
            System.out.println("User not found.");
        }
    }
}
