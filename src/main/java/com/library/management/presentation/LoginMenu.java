package com.library.management.presentation;


import com.library.management.model.AdminType;
import com.library.management.model.Administrator;
import com.library.management.model.Member;
import com.library.management.model.User;
import com.library.management.service.AuthenticationService;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code LoginMenu} class is the entry point of the Library Management System
 * and is part of the presentation layer. It provides a console-based interface
 * that allows users to log in, register as new members, reset forgotten passwords,
 * or exit the application.
 *
 * <p>
 * This class interacts with both the service layer and the presentation layer:
 * </p>
 * <ul>
 *   <li>Delegates user authentication to the {@link AuthenticationService}</li>
 *   <li>Handles new member registration via {@link SignUpMenu}</li>
 *   <li>Handles password reset via {@link ForgotPasswordMenu}</li>
 * </ul>
 *
 * <p>
 * Based on successful authentication, users are redirected to the appropriate
 * post-login menus such as {@link SuperAdminMenu}, {@link StandardAdminMenu},
 * or {@link MemberMenu} depending on their user role.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for capturing user input</li>
 *   <li>{@code AuthenticationService} — to validate user credentials</li>
 *   <li>{@code SignUpMenu} — to register new members</li>
 *   <li>{@code ForgotPasswordMenu} — to handle password recovery</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class LoginMenu {

    /** Scanner object used to capture user input from the console. */
    private final Scanner scanner;

    /** Handles the signup flow for new members in the presentation layer. */
    private final SignUpMenu signUpMenu;

    /** Service layer class responsible for user authentication logic. */
    private final AuthenticationService authenticationService;

    /** Handles password reset flow for users who have forgotten their credentials. */
    private final ForgotPasswordMenu forgotPasswordMenu;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code LoginMenu} instance and initializes all necessary
     * components required for login, signup, and password recovery flows.
     * <p>
     * This constructor sets up the shared {@code Scanner} for input handling
     * and creates instances of the corresponding service and presentation
     * layer classes used during authentication and user account management.
     * </p>
     */
    public LoginMenu() {
        this.scanner = new Scanner(System.in);
        this.signUpMenu = new SignUpMenu(scanner);
        this.authenticationService = new AuthenticationService();
        this.forgotPasswordMenu = new ForgotPasswordMenu(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the main login menu and routes user input to the appropriate action.
     * <p>
     * This method serves as the entry point of the application, continuously presenting
     * the user with options to log in, sign up, reset a forgotten password, or exit.
     * Based on the user’s input, it delegates operations to the appropriate classes:
     * </p>
     * <ul>
     *   <li>{@link AuthenticationService} for login</li>
     *   <li>{@link SignUpMenu} for new member registration</li>
     *   <li>{@link ForgotPasswordMenu} for password recovery</li>
     * </ul>
     *
     * <p>
     * The loop runs until the user chooses to exit the program.
     * </p>
     */
    public void display() {
        while (true) {
            // Display main options
            System.out.println("\n1. Login");
            System.out.println("2. Sign Up as a new Member");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit the Program");
            System.out.print("Enter your choice: ");

            String userChoiceStr = scanner.nextLine(); // Read raw user input

            try {
                int userChoice = Integer.parseInt(userChoiceStr); // Parse input as integer

                // Route to appropriate option based on input
                switch (userChoice) {
                    case 1 -> authenticateUser();                       // Login flow
                    case 2 -> signUpMenu.signUpNewMember();             // Sign-up flow
                    case 3 -> forgotPasswordMenu.resetPassword();       // Forgot password flow
                    case 4 -> exitProgram();                            // Exit application
                    default -> System.out.println("Invalid input.");    // Invalid option handling
                }
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Invalid input, enter a valid number."); // Handle non-numeric input
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Authenticates the user based on input credentials and redirects them
     * to the appropriate menu based on their role (super admin, standard admin, or member).
     * <p>
     * This method prompts the user for a username and password, delegates authentication
     * to the {@link AuthenticationService}, and conditionally loads one of the following menus:
     * </p>
     * <ul>
     *   <li>{@link SuperAdminMenu} if the user is a super admin</li>
     *   <li>{@link StandardAdminMenu} if the user is a standard admin</li>
     *   <li>{@link MemberMenu} if the user is a member</li>
     * </ul>
     * <p>
     * If authentication fails, an error message is displayed.
     * </p>
     */
    private void authenticateUser() {
        // Prompt for username
        System.out.print("\nEnter your username: ");
        String username = scanner.nextLine();

        // Prompt for password
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Attempt to authenticate the user using the service layer
        User authenticatedUser = authenticationService.authenticate(username, password);

        // Handle invalid login
        if (authenticatedUser == null) {
            System.out.println("Invalid credentials.");
        }

        // Handle admin users
        if (authenticatedUser instanceof Administrator) {
            Administrator administrator = (Administrator) authenticatedUser;

            // Check the type of admin and route accordingly
            if (administrator.getAdminType().equals(AdminType.SUPER)) {
                SuperAdminMenu superAdminMenu = new SuperAdminMenu(scanner);
                superAdminMenu.display(administrator);
            } else if (administrator.getAdminType().equals(AdminType.STANDARD)) {
                StandardAdminMenu standardAdminMenu = new StandardAdminMenu(scanner);
                standardAdminMenu.display(administrator);
            }
        }
        // Handle member users
        else if (authenticatedUser instanceof Member) {
            MemberMenu memberMenu = new MemberMenu(scanner);
            memberMenu.display((Member) authenticatedUser);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Terminates the program and displays a farewell message.
     * <p>
     * This method is called when the user chooses to exit the application
     * from the main login menu. It uses {@code System.exit(0)} to shut down
     * the JVM cleanly.
     * </p>
     */
    private void exitProgram() {
        System.out.println("Exiting the program. Goodbye!");
        exit(0); // Terminate the program with exit status 0 (normal termination)
    }

}
