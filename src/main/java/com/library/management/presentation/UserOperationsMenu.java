package com.library.management.presentation;

import com.library.management.model.Administrator;
import com.library.management.operation.UserOperations;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code UserOperationsMenu} class is part of the presentation layer of the
 * Library Management System and provides a command-line interface for managing
 * user-related operations.
 *
 * <p>
 * This class displays two different menus based on the role of the logged-in administrator:
 * <ul>
 *   <li><strong>Super Admin:</strong> Has access to full user management functionality, including
 *       adding admins, changing user types, and removing users.</li>
 *   <li><strong>Standard Admin:</strong> Has restricted permissions, limited to viewing and updating
 *       existing users and adding members.</li>
 * </ul>
 * </p>
 *
 * <p>
 * User selections from the menu are parsed and handled through corresponding methods in the
 * {@link com.library.management.operation.UserOperations} class, which encapsulates the
 * business logic for all user-related operations.
 * </p>
 *
 * <p>
 * This class also contains utility methods for reading validated user input and exiting the program.
 * </p>
 *
 * <p><strong>Responsibilities include:</strong></p>
 * <ul>
 *   <li>Displaying role-based user management menus</li>
 *   <li>Routing user input to the appropriate service methods</li>
 *   <li>Handling input validation and displaying feedback</li>
 *   <li>Terminating or returning from the menu as required</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class UserOperationsMenu {

    /** Scanner object for reading input from the user via console. */
    private final Scanner scanner;

    /** Handles business logic for user-related operations (add, update, delete, etc.). */
    private final UserOperations userOperations;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code UserOperationsMenu} with the specified {@code Scanner} for input.
     * <p>
     * Initializes the {@link UserOperations} instance using the same {@code Scanner} to
     * ensure consistent user interaction across the presentation and operation layers.
     * </p>
     *
     * @param scanner the {@code Scanner} object used to read user input from the console
     */
    public UserOperationsMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userOperations = new UserOperations(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the user management menu for super administrators and handles the selected operation.
     * <p>
     * Super admins have elevated privileges, including managing both members and other admins.
     * This method provides options such as:
     * </p>
     * <ul>
     *   <li>Adding members and admins</li>
     *   <li>Finding users by ID or username</li>
     *   <li>Viewing all users by type</li>
     *   <li>Updating user credentials, roles, and account details</li>
     *   <li>Viewing borrowing activity and removing users</li>
     * </ul>
     *
     * <p>
     * Based on the user's input, this method delegates each operation to the appropriate
     * method in the {@link com.library.management.operation.UserOperations} class.
     * </p>
     *
     * @param administrator the currently logged-in super admin, used to verify or authorize privileged operations
     */
    public void printSuperAdminUserOptions(Administrator administrator) {
        // Display the super admin user operations menu
        System.out.println("\n--- User Related Operations ---");
        System.out.println("\n1. Add a new member to the library");
        System.out.println("2. Add a new admin to the library");
        System.out.println("3. Find a user by ID");
        System.out.println("4. Find a user by username");
        System.out.println("5. View all members in the library");
        System.out.println("6. View all admins in the library");
        System.out.println("7. View all users in the library");
        System.out.println("8. Update the details of a user");
        System.out.println("9. Update a user's username");
        System.out.println("10. Update a user's password");
        System.out.println("11. Update a user's type");
        System.out.println("12. Remove a user from the library");
        System.out.println("13. View the user type of a user");
        System.out.println("14. View the admin type of an admin");
        System.out.println("15. View currently borrowed books by a user");
        System.out.println("16. View borrowing history of a user");
        System.out.println("17. Go back to the previous menu");
        System.out.println("18. Exit the program");

        // Get user choice
        int userChoice = readInput();

        // Execute operation based on selected choice
        switch (userChoice) {
            case 1 -> userOperations.addNewMember();                      // Add a new member
            case 2 -> userOperations.addNewAdmin(administrator);          // Add a new admin (requires super admin privileges)
            case 3 -> userOperations.findUserByUserId();                  // Search user by ID
            case 4 -> userOperations.findUserByUsername();                // Search user by username
            case 5 -> userOperations.viewAllMembers();                    // List all members
            case 6 -> userOperations.viewAllAdmins();                     // List all admins
            case 7 -> userOperations.viewAllUsers();                      // List all users (members + admins)
            case 8 -> userOperations.updateUser();                        // Update full user details
            case 9 -> userOperations.updateUsername();                    // Update username
            case 10 -> userOperations.updatePassword();                   // Update password
            case 11 -> userOperations.updateUserType(administrator);      // Change user type (requires super admin authority)
            case 12 -> userOperations.removeUser();                       // Remove user from system
            case 13 -> userOperations.viewUserType();                     // View user's type (admin/member)
            case 14 -> userOperations.viewAdminType();                    // View admin's role (standard/super)
            case 15 -> userOperations.viewBorrowedBooksByUser();          // Show books currently borrowed by user
            case 16 -> userOperations.viewUserBorrowingHistory();         // Show borrowing history of user
            case 17 -> {
                System.out.println("Returning to the main admin menu..."); // Go back to previous admin menu
                return;
            }
            case 18 -> exitProgram();                                       // Exit the program
            default -> System.out.println("Invalid input, try again.");     // Handle invalid menu selection
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the user management menu for standard administrators and handles the selected operation.
     * <p>
     * Standard admins have access to basic user-related operations such as:
     * </p>
     * <ul>
     *   <li>Adding new members</li>
     *   <li>Finding users by ID or username</li>
     *   <li>Viewing lists of members, admins, or all users</li>
     *   <li>Updating user information</li>
     *   <li>Viewing user types and borrowing activity</li>
     * </ul>
     *
     * <p>
     * This method restricts higher-level operations (like assigning user types or adding admins)
     * which are only available to super admins.
     * </p>
     *
     * @param administrator the currently logged-in standard admin (passed for consistency or future use)
     */
    public void printStandardAdminUserOptions(Administrator administrator) {
        // Display the standard admin's user-related menu options
        System.out.println("\n--- User Related Operations ---");
        System.out.println("\n1. Add a new member to the library");
        System.out.println("2. Find a user by ID");
        System.out.println("3. Find a user by username");
        System.out.println("4. View all members in the library");
        System.out.println("5. View all admins in the library");
        System.out.println("6. View all users in the library");
        System.out.println("7. Update the details of a user");
        System.out.println("8. View the user type of a user");
        System.out.println("9. View currently borrowed books by a user");
        System.out.println("10. View borrowing history of a user");
        System.out.println("11. Go back to the previous menu");
        System.out.println("12. Exit the program");

        // Get user input choice
        int userChoice = readInput();

        // Execute the corresponding operation based on input
        switch (userChoice) {
            case 1 -> userOperations.addNewMember();                 // Add a new member
            case 2 -> userOperations.findUserByUserId();             // Search user by ID
            case 3 -> userOperations.findUserByUsername();           // Search user by username
            case 4 -> userOperations.viewAllMembers();               // List all members
            case 5 -> userOperations.viewAllAdmins();                // List all admins
            case 6 -> userOperations.viewAllUsers();                 // List all users
            case 7 -> userOperations.updateUser();                   // Update user details
            case 8 -> userOperations.viewUserType();                 // Show user's type (admin/member)
            case 9 -> userOperations.viewBorrowedBooksByUser();      // Show books currently borrowed
            case 10 -> userOperations.viewUserBorrowingHistory();    // Show borrowing history
            case 11 -> {
                System.out.println("Returning to the main admin menu..."); // Go back to previous menu
                return;
            }
            case 12 -> exitProgram();                                  // Exit the program
            default -> System.out.println("Invalid input, try again."); // Handle invalid input
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Reads and parses numeric input from the user via console.
     * <p>
     * If the user enters a non-numeric value, an error message is displayed,
     * and {@code -1} is returned to indicate invalid input.
     * </p>
     *
     * @return the integer entered by the user, or {@code -1} if the input is not a valid number
     */
    private int readInput() {
        System.out.print("Enter your choice: ");
        String input = scanner.nextLine(); // Read raw input from the user

        try {
            return Integer.parseInt(input); // Attempt to parse input as integer
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a valid number."); // Inform the user of invalid input
            return -1; // Return sentinel value for invalid input
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gracefully terminates the application after displaying an exit message.
     * <p>
     * This method is called when the user chooses to exit from the menu.
     * It stops the program using {@code System.exit(0)}.
     * </p>
     */
    private void exitProgram() {
        System.out.println("Exiting the program. Goodbye!"); // Display exit message
        exit(0); // Terminate the application with status code 0 (normal termination)
    }

}
