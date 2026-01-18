package com.library.management.presentation;

import com.library.management.exception.UserNotFoundException;
import com.library.management.model.User;
import com.library.management.service.UserServiceImpl;
import com.library.management.util.EmailSender;
import com.library.management.validator.UserInputValidator;

import java.util.Random;
import java.util.Scanner;

/**
 * The {@code ForgotPasswordMenu} class is part of the presentation layer
 * of the Library Management System. It provides a console-based interface
 * for users who need to reset their forgotten passwords.
 *
 * <p>
 * The class performs the following responsibilities:
 * </p>
 * <ul>
 *   <li>Captures the username from the user</li>
 *   <li>Retrieves the corresponding {@link User} using {@link UserServiceImpl}</li>
 *   <li>Generates and sends a verification code to the user's registered email using {@link EmailSender}</li>
 *   <li>Validates the entered verification code</li>
 *   <li>Prompts the user for a new password and validates it using {@link UserInputValidator}</li>
 *   <li>Updates the password through the service layer</li>
 * </ul>
 *
 * <p>
 * This class integrates functionality from multiple layers of the application:
 * <ul>
 *   <li>Service layer: to retrieve and update user data</li>
 *   <li>Validation layer: to ensure input meets password rules</li>
 *   <li>Utility layer: to send email-based verification codes</li>
 * </ul>
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for capturing user input</li>
 *   <li>{@link UserServiceImpl} — to interact with user records</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class ForgotPasswordMenu {

    /** Scanner instance used for capturing user input from the console. */
    private final Scanner scanner;

    /** Service layer used to interact with user data for password reset functionality. */
    private final UserServiceImpl userService;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code ForgotPasswordMenu} with the given {@code Scanner} to handle user input.
     * <p>
     * Initializes the {@link UserServiceImpl} to support password reset operations.
     * </p>
     *
     * @param scanner the {@code Scanner} object used for reading input from the user
     */
    public ForgotPasswordMenu(Scanner scanner) {
        this.scanner = scanner;
        this.userService = new UserServiceImpl();
    }

    //-----------------------------------------------------------------------
    /**
     * Facilitates the password reset process for a user who has forgotten their password.
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *     <li>Prompts the user to enter their username</li>
     *     <li>Retrieves the corresponding {@link User} using the {@link UserServiceImpl}</li>
     *     <li>Generates a numeric verification code and sends it via email using {@link EmailSender}</li>
     *     <li>Asks the user to enter the received verification code</li>
     *     <li>If verified, prompts for a new password and validates it using {@link UserInputValidator}</li>
     *     <li>Updates the password if all validations pass</li>
     * </ol>
     * <p>
     * If any step fails (user not found, incorrect verification code, or invalid password),
     * the process exits with a suitable message.
     * </p>
     */
    public void resetPassword() {
        // Step 1: Ask for the username
        System.out.print("\nEnter your username: ");
        String username = scanner.nextLine();

        try {
            // Step 2: Retrieve the user from the service layer
            User user = userService.getUserByUsername(username);

            if (user == null) {
                System.out.println("Username '" + username + "' does not exist in the system.\n");
                return;
            }

            // Step 3: Generate and send verification code
            String generatedCode = generateCode();
            EmailSender.sendEmail(user.getEmail(), generatedCode);

            // Step 4: Prompt user to enter the code
            System.out.print("Enter verification code sent to your email: ");
            String userVerificationInputStr = scanner.nextLine().trim();

            try {
                int userVerificationInput = Integer.parseInt(userVerificationInputStr);
                int generatedVerificationCode = Integer.parseInt(generatedCode);

                // Step 5: Verify the code
                if (userVerificationInput != generatedVerificationCode) {
                    System.out.println("Incorrect verification code, try again.\n");
                    return;
                }

                System.out.println("Code verified.\n");

                // Step 6: Ask for new password
                System.out.print("Enter new password: ");
                String password = scanner.nextLine();

                // Step 7: Validate the password
                if (!UserInputValidator.isValidPassword(password)) {
                    System.out.println("Password does not meet the requirements.\n" +
                            "Password must contain at least one digit, " +
                            "one lower and upper case and one special character.");
                    return;
                }

                // Step 8: Update password in the database
                boolean passwordUpdated = userService.updatePassword(user.getUserId(), password);

                if (passwordUpdated) {
                    System.out.println("Your password has been successfully updated.\n");
                } else {
                    System.out.println("Password could not be updated due to some error.\n");
                }

            } catch (NumberFormatException numberFormatException) {
                System.out.println("Invalid input, enter a valid number.\n");
            }

        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Generates a random 6-digit numeric verification code as a {@link String}.
     * <p>
     * This method is typically used for email-based verification during
     * password reset flows.
     * </p>
     *
     * @return a randomly generated 6-digit number in string format
     */
    private String generateCode() {
        Random random = new Random();

        // Generate a random number between 100000 and 999999 (inclusive)
        int sixDigit = 100000 + random.nextInt(900000);

        // Convert the number to a string and return it
        return Integer.toString(sixDigit);
    }

}
