package com.library.management.presentation;

import com.library.management.model.Administrator;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code StandardAdminMenu} class is part of the presentation layer of the
 * Library Management System and serves as the main menu interface for standard administrators.
 *
 * <p>
 * This class provides a console-based navigation menu that allows standard admins to:
 * </p>
 * <ul>
 *   <li>Perform user-related operations via {@link UserOperationsMenu}</li>
 *   <li>Perform book-related operations via {@link BookOperationsMenu}</li>
 *   <li>Logout from the current session</li>
 *   <li>Exit the application</li>
 * </ul>
 *
 * <p>
 * Unlike super admins, standard admins have restricted access and cannot perform elevated tasks
 * such as adding new admin users or modifying user roles.
 * </p>
 *
 * <p>
 * Internally, the class captures and processes console input using a {@code Scanner}, and delegates
 * operation handling to appropriate menu classes based on the admin’s selection.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — used to capture user input from the console</li>
 *   <li>{@code UserOperationsMenu} — manages user-related tasks</li>
 *   <li>{@code BookOperationsMenu} — manages book-related tasks</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class StandardAdminMenu {

    /** Scanner object used to capture input from the console. */
    private final Scanner scanner;

    /** Handles standard admin-related user operations through the user interface. */
    private final UserOperationsMenu userOperationsMenu;

    /** Handles book-related operations through the admin interface. */
    private final BookOperationsMenu bookOperationsMenu;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code StandardAdminMenu} with the specified {@code Scanner} for reading input.
     * <p>
     * Initializes the user and book operation menus with the shared {@code Scanner}
     * to ensure consistent user input handling across the session.
     * </p>
     *
     * @param scanner the {@code Scanner} object used for console input
     */
    public StandardAdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userOperationsMenu = new UserOperationsMenu(scanner);
        this.bookOperationsMenu = new BookOperationsMenu(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays a welcome message and continuously shows the standard admin options menu.
     * <p>
     * This method serves as the main loop for the standard admin session. It greets the
     * logged-in administrator by name and repeatedly invokes the standard admin menu
     * until the user chooses to log out or exit the application.
     * </p>
     *
     * @param administrator the currently logged-in standard administrator
     */
    public void display(Administrator administrator) {
        // Greet the admin with their first name
        System.out.println("\nWelcome " + administrator.getFirstname() + "! What do you want to do today?");

        // Loop to continually present admin options until logout or exit
        while (true) {
            printAdminOptions(administrator); // Route to main admin options handler
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the main menu options for the standard admin and routes the selected action
     * to the corresponding handler method or menu.
     * <p>
     * This method allows standard admins to:
     * </p>
     * <ul>
     *   <li>Access user-related operations via {@link UserOperationsMenu}</li>
     *   <li>Access book-related operations via {@link BookOperationsMenu}</li>
     *   <li>Logout and return to the login screen</li>
     *   <li>Exit the program completely</li>
     * </ul>
     *
     * @param administrator the currently logged-in standard admin
     */
    private void printAdminOptions(Administrator administrator) {
        // Display menu options available to standard admin
        System.out.println("\n1. User Related Operations");
        System.out.println("2. Book Related Operations");
        System.out.println("3. Logout");
        System.out.println("4. Exit the program");

        try {
            int userChoice = readInput(); // Read user input from console

            // Route to appropriate submenu or action based on input
            switch (userChoice) {
                case 1 -> userOperationsMenu.printStandardAdminUserOptions(administrator); // Go to user-related operations
                case 2 -> bookOperationsMenu.printAdminBookOptions();                      // Go to book-related operations
                case 3 -> logout();                                                        // Log out and return to login
                case 4 -> exitProgram();                                                   // Exit the application
                default -> System.out.println("Invalid input, try again.");               // Handle invalid menu option
            }
        } catch (NumberFormatException e) {
            // Additional safeguard if input parsing fails
            System.out.println("Invalid input, please enter a valid number.");
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
