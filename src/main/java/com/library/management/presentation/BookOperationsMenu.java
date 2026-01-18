package com.library.management.presentation;

import com.library.management.operation.BookOperations;

import java.util.Scanner;

import static java.lang.System.exit;

/**
 * The {@code BookOperationsMenu} class belongs to the presentation layer of the
 * Library Management System and serves as the console-based user interface for
 * handling all administrative book-related tasks.
 *
 * <p>
 * This class presents a structured menu to the admin user, captures and validates
 * input using a {@code Scanner}, and delegates the corresponding actions to the
 * {@link com.library.management.operation.BookOperations} class.
 * </p>
 *
 * <p>
 * The available operations include:
 * <ul>
 *   <li>Adding, updating, and removing books</li>
 *   <li>Searching and filtering books by ID, title, author, or genre</li>
 *   <li>Viewing all books, available books, or borrowed books</li>
 *   <li>Updating a specific field of a book</li>
 * </ul>
 * </p>
 *
 * <p>
 * The class also handles input validation and displays appropriate error messages
 * when invalid input is detected. It includes functionality to exit the application
 * or return to the previous menu.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code Scanner} — for reading user input from the console.</li>
 *   <li>{@code BookOperations} — for executing book-related business logic.</li>
 * </ul>
 *
 * @author Fardaws Jawad
 */
public class BookOperationsMenu {

    /**
     Scanner object for reading user input from the console.
     */
    private final Scanner scanner;

    /**
     Handles core operations related to books (add, update, delete, search, etc.).
     */
    private final BookOperations bookOperations;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code BookOperationsMenu} with the specified {@code Scanner} for user input.
     * <p>
     * Initializes the {@link BookOperations} instance using the same {@code Scanner} to ensure
     * consistent user interaction across the presentation and operation layers.
     * </p>
     *
     * @param scanner the {@code Scanner} object used to read user input from the console
     */
    public BookOperationsMenu(Scanner scanner) {
        this.scanner = scanner;
        bookOperations = new BookOperations(scanner);
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the book operations menu for the admin user and handles user input to perform
     * corresponding book-related actions.
     * <p>
     * This menu allows the admin to manage books in the library, including adding, updating,
     * viewing, searching, and deleting books, as well as viewing borrowed and available books.
     * </p>
     *
     * <p>
     * Based on the selected option, this method delegates the operation to the
     * {@link com.library.management.operation.BookOperations} class.
     * </p>
     *
     * <p>
     * Option 13 returns to the previous admin menu, and option 14 exits the application.
     * </p>
     */
    public void printAdminBookOptions() {
        // Displaying the list of available book operations for the admin
        System.out.println("\n--- Book Related Operations ---");
        System.out.println("\n1. Add a book to the library");
        System.out.println("2. Find a book by ID");
        System.out.println("3. View all books in the library");
        System.out.println("4. Update the details of a book");
        System.out.println("5. Remove a book from the library");
        System.out.println("6. View all borrowed books from the library");
        System.out.println("7. View available books in the library");
        System.out.println("8. View books by genre");
        System.out.println("9. View book by title");
        System.out.println("10. View books by author");
        System.out.println("11. Search for books in the library");
        System.out.println("12. Update a specific field of a book");
        System.out.println("13. Go back to the previous menu");
        System.out.println("14. Exit the program");

        // Read user input for menu choice
        int userChoice = readInput();

        // Execute the corresponding book operation based on the admin's choice
        switch (userChoice) {
            case 1 -> bookOperations.addNewBook();                // Add a new book to the system
            case 2 -> bookOperations.findBookById();              // Find a book by its unique ID
            case 3 -> bookOperations.viewAllBooks();              // List all books in the library
            case 4 -> bookOperations.updateBook();                // Update full book details
            case 5 -> bookOperations.removeBook();                // Remove a book from the catalog
            case 6 -> bookOperations.viewAllBorrowedBooks();      // Show all currently borrowed books
            case 7 -> bookOperations.viewAvailableBooks();        // Show books that are currently available
            case 8 -> bookOperations.viewBooksByGenre();          // List books by a selected genre
            case 9 -> bookOperations.viewBookByTitle();           // Search for books by title
            case 10 -> bookOperations.viewBooksByAuthor();        // Search for books by author
            case 11 -> bookOperations.searchBooks();              // General search through multiple fields
            case 12 -> bookOperations.updateBookField();          // Update a single field (e.g., title, author)
            case 13 -> {
                System.out.println("Returning to the main admin menu...");
                return; // Exit this menu and return to the previous one
            }
            case 14 -> exitProgram(); // Terminate the program
            default -> System.out.println("Invalid input, try again."); // Handle unexpected input
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the user to enter a numeric input and parses it as an integer.
     * <p>
     * If the input is not a valid number, an error message is shown and {@code -1} is returned.
     * This allows the calling method to handle invalid input gracefully.
     * </p>
     *
     * @return the parsed integer input, or {@code -1} if the input is invalid
     */
    private int readInput() {
        System.out.print("Enter your choice: ");
        String input = scanner.nextLine();

        try {
            return Integer.parseInt(input); // Try to convert user input to an integer
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a valid number."); // Show error if parsing fails
            return -1; // Return sentinel value to indicate invalid input
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Terminates the program after displaying an exit message.
     * <p>
     * This method is typically called when the user chooses to exit
     * the application from the menu. It calls {@code System.exit(0)}
     * to stop the JVM.
     * </p>
     */
    private void exitProgram() {
        System.out.println("Exiting the program. Goodbye!"); // Display farewell message to user
        exit(0); // Terminate the application gracefully
    }
}
