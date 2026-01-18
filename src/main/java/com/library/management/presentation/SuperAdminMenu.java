package com.library.management.presentation;

import com.library.management.model.Administrator;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code SuperAdminMenu} class belongs to the presentation layer of the
 * Library Management System and serves as the main entry point for super administrators.
 *
 * <p>
 * This class provides a console-based interface that allows super admins to:
 * </p>
 * <ul>
 *   <li>Access user-related operations through {@link UserOperationsMenu}</li>
 *   <li>Access book-related operations through {@link BookOperationsMenu}</li>
 *   <li>Log out and return to the login screen</li>
 *   <li>Exit the application</li>
 * </ul>
 *
 * <p>
 * The class continuously interacts with the admin user, reads validated input from
 * the console, and delegates tasks to the corresponding operation menus.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for capturing console input</li>
 *   <li>{@code UserOperationsMenu} — for handling user management tasks</li>
 *   <li>{@code BookOperationsMenu} — for handling book management tasks</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class SuperAdminMenu {

    /** Scanner object used to read user input from the console. */
    private final Scanner scanner;

    /** Menu handler for all user-related operations accessible to the super admin. */
    private final UserOperationsMenu userOperationsMenu;

    /** Menu handler for all book-related operations accessible to the super admin. */
    private final BookOperationsMenu bookOperationsMenu;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code SuperAdminMenu} with the specified {@code Scanner} for input.
     * <p>
     * Initializes submenus for user operations and book operations, passing the scanner
     * to ensure consistent user input handling across the application.
     * </p>
     *
     * @param scanner the {@code Scanner} object used to capture console input
     */
    public SuperAdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userOperationsMenu = new UserOperationsMenu(scanner);
        this.bookOperationsMenu = new BookOperationsMenu(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays a welcome message and repeatedly presents the super admin options menu.
     * <p>
     * This method acts as the entry point for the super admin session. It greets the logged-in
     * administrator by name and continuously invokes the admin menu until the application is exited
     * or returned from within the called method.
     * </p>
     *
     * @param administrator the currently logged-in super admin user
     */
    public void display(Administrator administrator) {
        System.out.println("\nWelcome " + administrator.getFirstname() + "! What do you want to do today?");
        while (true) {
            printAdminOptions(administrator);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the main menu options for the super admin and routes the selected option
     * to the appropriate handler.
     * <p>
     * This menu allows the super admin to:
     * </p>
     * <ul>
     *   <li>Perform user-related operations via {@link UserOperationsMenu}</li>
     *   <li>Perform book-related operations via {@link BookOperationsMenu}</li>
     *   <li>Logout and return to the previous menu or login</li>
     *   <li>Exit the application entirely</li>
     * </ul>
     *
     * @param administrator the currently logged-in super admin user
     */
    private void printAdminOptions(Administrator administrator) {
        // Display available super admin options
        System.out.println("\n1. User Related Operations");
        System.out.println("2. Book Related Operations");
        System.out.println("3. Logout");
        System.out.println("4. Exit the program");

        try {
            int userChoice = readInput(); // Read user input from console

            // Route to appropriate action based on user input
            switch (userChoice) {
                case 1 -> userOperationsMenu.printSuperAdminUserOptions(administrator); // Route to user operations
                case 2 -> bookOperationsMenu.printAdminBookOptions();                    // Route to book operations
                case 3 -> logout();                                                      // Handle logout
                case 4 -> exitProgram();                                                 // Exit the application
                default -> System.out.println("Invalid input, try again.");             // Handle invalid input
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a valid number."); // Fallback for invalid number format
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Logs the super admin out of the current session and redirects to the login screen.
     * <p>
     * This method clears the current admin session context and re-invokes the {@link LoginMenu}
     * to allow another user to log in or exit.
     * </p>
     */
    private void logout() {
        System.out.println("You have been logged out. Returning to login screen...\n");

        // Redirect to the login menu
        LoginMenu loginMenu = new LoginMenu();
        loginMenu.display();
    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the super admin to enter a numeric option and parses it as an integer.
     * <p>
     * If the input is not a valid number, an error message is displayed and {@code -1} is returned.
     * This helps in gracefully handling invalid inputs during menu navigation.
     * </p>
     *
     * @return the integer entered by the user, or {@code -1} if input is invalid
     */
    private int readInput() {
        System.out.print("Enter your choice: ");
        String input = scanner.nextLine();

        try {
            return Integer.parseInt(input); // Try converting input to integer
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a valid number."); // Handle non-numeric input
            return -1; // Indicate invalid input
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gracefully terminates the application after displaying a farewell message.
     * <p>
     * This method is called when the super admin chooses to exit the program entirely.
     * It ends the JVM process with a status code of {@code 0}, indicating normal termination.
     * </p>
     */
    private void exitProgram() {
        System.out.println("Exiting the program. Goodbye!"); // Display exit message
        exit(0); // Terminate the application
    }

}
