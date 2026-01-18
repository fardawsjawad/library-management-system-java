package com.library.management.presentation;

import com.library.management.model.Member;
import com.library.management.operation.BookOperations;
import com.library.management.operation.UserOperations;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code MemberMenu} class belongs to the presentation layer of the
 * Library Management System and provides a console-based interface for members.
 *
 * <p>
 * This class allows registered members to interact with the system by:
 * </p>
 * <ul>
 *   <li>Borrowing and returning books</li>
 *   <li>Viewing currently borrowed books and borrowing history</li>
 *   <li>Searching and browsing books by various filters</li>
 *   <li>Updating their profile details</li>
 *   <li>Logging out or exiting the system</li>
 * </ul>
 *
 * <p>
 * Internally, it delegates business operations to the {@link UserOperations}
 * and {@link BookOperations} classes, which in turn communicate with the service
 * and DAO layers for logic execution and data persistence.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for capturing console input</li>
 *   <li>{@code BookOperations} — for handling book-related functionality</li>
 *   <li>{@code UserOperations} — for handling user-related functionality</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class MemberMenu {

    /** Scanner object used to read input from the console. */
    private final Scanner scanner;

    /** Handles book-related operations requested by the member. */
    private final BookOperations bookOperations;

    /** Handles user-related operations requested by the member. */
    private final UserOperations userOperations;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code MemberMenu} with the specified {@code Scanner} for input handling.
     * <p>
     * Initializes dependencies for book and user operations to allow members to
     * interact with the system via the console.
     * </p>
     *
     * @param scanner the {@code Scanner} object used to capture input from the member
     */
    public MemberMenu(Scanner scanner) {
        this.scanner = scanner;
        this.bookOperations = new BookOperations(scanner);
        this.userOperations = new UserOperations(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays a personalized welcome message and continuously presents the member options menu.
     * <p>
     * This method serves as the main loop for the member session. It greets the logged-in
     * member by their first name and continuously invokes the member menu until the user
     * chooses to log out or exit.
     * </p>
     *
     * @param member the currently logged-in library member
     */
    public void display(Member member) {
        // Greet the member with their first name
        System.out.println("\nWelcome " + member.getFirstname() + "! What do you want to do today?");

        // Continuously display the member options menu
        while (true) {
            printMemberOptions(member.getUserId()); // Pass the member’s ID to the menu handler
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the member operations menu and routes the selected option
     * to the corresponding handler method.
     * <p>
     * This method provides members with access to book borrowing, returning, viewing history,
     * profile management, and book exploration features. The operations are delegated to
     * {@link BookOperations} and {@link UserOperations} classes.
     * </p>
     *
     * @param memberId the unique ID of the currently logged-in member
     */
    private void printMemberOptions(int memberId) {
        // Display menu options for members
        System.out.println("\n1. Borrow a Book");
        System.out.println("2. Return a Book");
        System.out.println("3. View books you have currently borrowed");
        System.out.println("4. View Borrowing History");
        System.out.println("5. Update Profile");
        System.out.println("6. View All Books in the Library");
        System.out.println("7. View Available Books in the Library");
        System.out.println("8. View Books by Genre");
        System.out.println("9. View Book by Title");
        System.out.println("10. View Books by Author");
        System.out.println("11. Search for Books in the Library");
        System.out.println("12. Logout");
        System.out.println("13. Exit the program");

        int userChoice = readInput(); // Get user input

        // Route user input to the correct operation
        switch (userChoice) {
            case 1 -> bookOperations.borrowBook(memberId);
            case 2 -> bookOperations.returnBook(memberId);
            case 3 -> userOperations.viewMemberBorrowedBooks(memberId);
            case 4 -> userOperations.viewMemberBorrowingHistory(memberId);
            case 5 -> userOperations.updateMemberProfile(memberId);
            case 6 -> bookOperations.viewAllBooks();
            case 7 -> bookOperations.viewAvailableBooks();
            case 8 -> bookOperations.viewBooksByGenre();
            case 9 -> bookOperations.viewBookByTitle();
            case 10 -> bookOperations.viewBooksByAuthor();
            case 11 -> bookOperations.searchBooks();
            case 12 -> logout();         // End session and return to login
            case 13 -> exitProgram();    // Terminate application
            default -> System.out.println("Invalid input, try again.");
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
