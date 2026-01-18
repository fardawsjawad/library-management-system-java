package com.library.management.operation;

import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.NoAvailableCopiesException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.model.Book;
import com.library.management.model.Status;
import com.library.management.model.Transaction;
import com.library.management.service.BookServiceImpl;
import com.library.management.service.TransactionServiceImpl;
import com.library.management.validator.BookInputValidator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Handles all book-related operations in the Library Management System.
 * <p>
 * The {@code BookOperations} class acts as the intermediary between the presentation layer
 * (e.g., console or GUI) and the service layer, providing interactive functionalities
 * for administrators and members to manage and interact with books in the library.
 * It uses the {@code BookServiceImpl} and {@code TransactionServiceImpl} to delegate
 * business logic, and leverages validation utilities to ensure data integrity.
 * </p>
 *
 * <p><strong>Core Functionalities:</strong></p>
 * <ul>
 *   <li><strong>Book Management:</strong> Add, update (full or field-based), delete, and search books</li>
 *   <li><strong>Book Queries:</strong> View books by ID, title, author, genre, availability, or keyword</li>
 *   <li><strong>Borrowing:</strong> Allows a member to borrow a book if eligible</li>
 *   <li><strong>Returning:</strong> Allows a member to return a previously borrowed book</li>
 * </ul>
 *
 * <p><strong>Design Notes:</strong></p>
 * <ul>
 *   <li>Uses {@code Scanner} for reading user input from the console</li>
 *   <li>Includes validation before passing data to the service layer</li>
 *   <li>Handles exceptions and provides user-friendly error messages</li>
 *   <li>Includes exit functionality to gracefully terminate the program</li>
 * </ul>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@link com.library.management.service.BookServiceImpl}</li>
 *   <li>{@link com.library.management.service.TransactionServiceImpl}</li>
 *   <li>{@link com.library.management.model.Book}</li>
 *   <li>{@link com.library.management.model.Transaction}</li>
 *   <li>{@link com.library.management.model.Status}</li>
 *   <li>{@link com.library.management.validator.BookInputValidator}</li>
 * </ul>
 *
 * <p>This class is typically instantiated in the main menu or user interface layer and invoked
 * based on user choices.</p>
 *
 * @author Fardaws Jawad
 */
public class BookOperations {

    /** Scanner instance for reading user input from the console */
    private final Scanner scanner;

    /** Service for handling core book-related logic */
    private final BookServiceImpl bookService;

    /** Service for handling transaction-related logic such as issuing and returning books */
    private final TransactionServiceImpl transactionService;

    //-----------------------------------------------------------------------
    /**
     * Constructs a {@code BookOperations} object with a specified {@code Scanner}.
     * Initializes the book and transaction service implementations.
     *
     * @param scanner the {@code Scanner} object used to read user input
     */
    public BookOperations(Scanner scanner) {
        this.scanner = scanner;

        // Initialize the service layer dependencies
        this.bookService = new BookServiceImpl();
        this.transactionService = new TransactionServiceImpl();
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new book to the library system.
     * <p>
     * This method prompts the user to enter book details such as title, author, genre,
     * ISBN, and the number of total copies. Each input is validated using the
     * {@code BookInputValidator} class. If all validations pass, the book is added via
     * the {@code BookServiceImpl}. Feedback is provided to the user after each step.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code NumberFormatException} – if the input for total copies is not a valid integer</li>
     *   <li>{@code IllegalArgumentException} – if any business logic in the service layer throws an error</li>
     * </ul>
     */
    public void addNewBook() {
        System.out.println("\n--- Add a New Book ---");

        // Prompt and validate the book title
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();
        if (!BookInputValidator.isValidTitle(title)) {
            System.out.println("Invalid title, you must provide a book title.");
            return;
        }

        // Prompt and validate the author's name
        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();
        if (!BookInputValidator.isValidAuthor(author)) {
            System.out.println("Invalid author name, you must provide an author for the book.");
            return;
        }

        // Prompt and validate the genre
        System.out.print("Enter book genre: ");
        String genre = scanner.nextLine().trim();
        if (!BookInputValidator.isValidGenre(genre)) {
            System.out.println("Invalid genre, genre must be less than 50 characters and not empty.");
            return;
        }

        // Prompt and validate the ISBN
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine().trim();
        if (!BookInputValidator.isValidIsbn(isbn)) {
            System.out.println("Invalid ISBN, must be 9 digits with the 10th index either a digit or a character," +
                    "or the ISBN must be exactly 13 digits.");
            return;
        }

        // Prompt and validate the total number of copies
        System.out.print("Enter number of total copies: ");
        String numberOfTotalCopiesStr = scanner.nextLine().trim();
        if (!BookInputValidator.isValidNumberOfTotalCopies(numberOfTotalCopiesStr)) {
            System.out.println("Number of total copies must be greater than 0.");
            return;
        }

        try {
            // Parse the number of copies from string to integer
            int numberOfTotalCopies = Integer.parseInt(numberOfTotalCopiesStr);

            // Create a new Book object with all required fields
            Book book = new Book(
                    title, author, genre, isbn,
                    true, numberOfTotalCopies, numberOfTotalCopies
            );

            // Call the service to add the book to the library
            boolean bookAdded = bookService.addBook(book);

            // Provide feedback based on the result
            if (bookAdded) {
                System.out.println("Book has been successfully added to the library.\n");
            } else {
                System.out.println("Book could not be added to the library.\n");
            }

        } catch (NumberFormatException numberFormatException) {
            // Handle invalid numeric input for total copies
            System.out.println("Error: " + numberFormatException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle any validation or business logic errors from the service layer
            System.out.println(illegalArgumentException.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Finds and displays a book based on its ID.
     * <p>
     * This method prompts the user to enter a book ID, validates it, and
     * retrieves the corresponding {@code Book} from the system using
     * {@code BookServiceImpl}. It handles scenarios where the book ID is invalid,
     * the book is not found, or the input is improperly formatted.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code BookNotFoundException} – if no book is found with the given ID</li>
     *   <li>{@code IllegalArgumentException} – if the input violates service-level constraints</li>
     * </ul>
     */
    public void findBookById() {
        System.out.println("\n--- Find Book by ID ---");

        // Prompt user for the book ID
        System.out.print("Enter book ID: ");
        String bookIdStr = scanner.nextLine().trim();

        // Validate the book ID input
        if (!BookInputValidator.isValidBookId(bookIdStr)) {
            System.out.println("Invalid book ID.");
            return;
        }

        try {
            // Parse the book ID from string to integer
            int bookId = Integer.parseInt(bookIdStr);

            // Retrieve the book using the service layer
            Book book = bookService.getBookById(bookId);

            // Display the retrieved book
            System.out.println("\n" + book);

        } catch (BookNotFoundException bookNotFoundException) {
            // Handle the case where the book ID does not exist in the system
            System.out.println(bookNotFoundException.getMessage() + "\n");

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle any validation/business rule violations in the service layer
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books available in the library.
     * <p>
     * This method retrieves the list of all books from the system using
     * {@code BookServiceImpl}. If the list is not empty, it sorts the books
     * alphabetically by title and displays them. If no books exist, it informs
     * the user accordingly.
     * </p>
     */
    public void viewAllBooks() {
        // Retrieve all books from the service layer
        List<Book> bookList = bookService.getAllBooks();

        if (!bookList.isEmpty()) {
            // Sort books alphabetically by title for better readability
            bookList.sort(Comparator.comparing(Book::getTitle));

            // Display the list of books
            System.out.println("\n--- Books in the Library ---");
            bookList.forEach(System.out::println);
            System.out.println();

        } else {
            // Inform the user if the library has no books
            System.out.println("No books exist in the library.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the details of an existing book in the library.
     * <p>
     * This method prompts the user to enter the book ID and new details including
     * title, author, genre, ISBN, and the total number of copies. It validates each input
     * and ensures the total number of copies is not less than the number of currently borrowed copies.
     * If validations pass, the book is updated via {@code BookServiceImpl}.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code BookNotFoundException} – if the book does not exist in the system</li>
     *   <li>{@code IllegalArgumentException} – if any input or business rule is violated</li>
     * </ul>
     */
    public void updateBook() {
        System.out.println("\n--- Update Book ---");

        // Prompt for book ID and validate it
        System.out.print("Enter book ID: ");
        String bookIdStr = scanner.nextLine().trim();
        if (!BookInputValidator.isValidBookId(bookIdStr)) {
            System.out.println("Invalid book ID.");
            return;
        }

        int bookId = Integer.parseInt(bookIdStr);

        // Check if the book with given ID exists
        if (!bookService.bookExists(bookId)) {
            System.out.println("Book with the ID " + bookId + " does not exist.");
            return;
        }

        // Prompt for updated book title and validate
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();
        if (!BookInputValidator.isValidTitle(title)) {
            System.out.println("Invalid title, you must provide a book title.");
            return;
        }

        // Prompt for updated author name and validate
        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();
        if (!BookInputValidator.isValidAuthor(author)) {
            System.out.println("Invalid author name, you must provide an author for the book.");
            return;
        }

        // Prompt for updated genre and validate
        System.out.print("Enter book genre: ");
        String genre = scanner.nextLine().trim();
        if (!BookInputValidator.isValidGenre(genre)) {
            System.out.println("Invalid genre, genre must be less than 50 characters and not empty.");
            return;
        }

        // Prompt for updated ISBN and validate
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine().trim();
        if (!BookInputValidator.isValidIsbn(isbn)) {
            System.out.println("Invalid ISBN, must be 9 digits with the 10th index either a digit or a character," +
                    "or the ISBN must be exactly 13 digits.");
            return;
        }

        // Prompt for updated number of total copies and validate
        System.out.print("Enter number of total copies: ");
        String numberOfTotalCopiesStr = scanner.nextLine().trim();
        if (!BookInputValidator.isValidNumberOfTotalCopies(numberOfTotalCopiesStr)) {
            System.out.println("Number of total copies must be greater than 0.");
            return;
        }

        try {
            int totalCopies = Integer.parseInt(numberOfTotalCopiesStr);

            // Calculate borrowed copies based on existing data
            int borrowedCopies = bookService.getTotalCopies(bookId)
                    - bookService.getAvailableCopies(bookId);

            // Ensure new total copies is not less than borrowed copies
            if (totalCopies < borrowedCopies) {
                throw new IllegalArgumentException("Total copies cannot be less than borrowed copies.");
            }

            // Calculate updated available copies
            int availableCopies = totalCopies - borrowedCopies;

            // Determine availability status
            boolean isAvailable = availableCopies > 0;

            // Construct updated Book object
            Book book = new Book(
                    bookId, title, author, genre, isbn,
                    isAvailable, totalCopies, availableCopies
            );

            // Attempt to update the book via the service
            boolean bookUpdated = bookService.updateBook(book);

            if (bookUpdated) {
                System.out.println("Book details successfully updated.\n");
            } else {
                System.out.println("Book details could not be updated.\n");
            }
        } catch (BookNotFoundException bookNotFoundException) {
            // Book no longer exists at the time of update
            System.out.println(bookNotFoundException.getMessage() + "\n");

        } catch (IllegalArgumentException illegalArgumentException) {
            // Any validation or logical error
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Removes a book from the library based on its ID.
     * <p>
     * This method prompts the user to enter a book ID and confirms whether the book
     * should be deleted. It performs input validation and ensures that the book exists
     * before attempting to delete it through the {@code BookServiceImpl}.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code BookNotFoundException} – if the book to be deleted does not exist</li>
     *   <li>{@code IllegalArgumentException} – if input or business validation fails</li>
     * </ul>
     */
    public void removeBook() {
        System.out.println("\n--- Remove a Book ---");

        // Prompt the user for the book ID
        System.out.print("Enter book ID: ");
        String bookIdStr = scanner.nextLine().trim();

        // Validate the book ID format
        if (!BookInputValidator.isValidBookId(bookIdStr)) {
            System.out.println("Invalid book ID.");
            return;
        }

        int bookId = Integer.parseInt(bookIdStr);

        try {
            // Check if the book exists before proceeding with deletion
            if (!bookService.bookExists(bookId)) {
                System.out.println("Book not found.\n");
                return;
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch any exception due to invalid checks or business rules
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }

        // Ask the user for confirmation before deleting the book
        System.out.print("Are you sure you want to delete this book? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Book deletion cancelled.\n");
            return;
        }

        try {
            // Attempt to delete the book through the service
            boolean bookRemoved = bookService.deleteBook(bookId);

            if (bookRemoved) {
                System.out.println("Book successfully removed.\n");
            } else {
                System.out.println("Book could not be removed.\n");
            }

        } catch (BookNotFoundException bookNotFoundException) {
            // Handle the case where the book no longer exists at deletion time
            System.out.println(bookNotFoundException.getMessage() + "\n");

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle any input or business logic issues
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books that are currently borrowed from the library.
     * <p>
     * This method retrieves a list of all borrowed books from the {@code BookServiceImpl}.
     * If the list is not empty, it sorts the books alphabetically by title and displays them.
     * If no books are currently borrowed, it notifies the user.
     * </p>
     */
    public void viewAllBorrowedBooks() {
        // Retrieve the list of all currently borrowed books
        List<Book> bookList = bookService.getAllBorrowedBooks();

        if (!bookList.isEmpty()) {
            // Sort the list alphabetically by title for better readability
            bookList.sort(Comparator.comparing(Book::getTitle));

            // Display the sorted list of borrowed books
            System.out.println("\n--- All Books Currently Borrowed from the Library ---");
            bookList.forEach(System.out::println);
            System.out.println();
        } else {
            // Inform the user that no books are currently borrowed
            System.out.println("No book has been borrowed from the library at this time.");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books that are currently available in the library.
     * <p>
     * This method retrieves the list of books that have at least one available copy
     * using the {@code BookServiceImpl}. If books are available, it sorts them
     * alphabetically by title and prints them. Otherwise, it informs the user that
     * all copies are currently borrowed.
     * </p>
     */
    public void viewAvailableBooks() {
        // Retrieve the list of books with available copies
        List<Book> bookList = bookService.getAvailableBooks();

        if (!bookList.isEmpty()) {
            // Sort the available books by title for better presentation
            bookList.sort(Comparator.comparing(Book::getTitle));

            // Display the list of available books
            System.out.println("\n--- Currently Available Books in the Library ---");
            bookList.forEach(System.out::println);
            System.out.println();
        } else {
            // Notify user if no books are currently available
            System.out.println("No available books in the library. All book copies have been borrowed.");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books in the library that belong to a specified genre.
     * <p>
     * This method prompts the user to enter a genre, validates the input,
     * and retrieves matching books using {@code BookServiceImpl}. If books exist
     * in the specified genre, they are displayed. Otherwise,
     * the user is informed that no books are available in that genre.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code IllegalArgumentException} – if the service layer detects any issue with the genre input</li>
     * </ul>
     */
    public void viewBooksByGenre() {
        System.out.println("\n--- View Books By Genre ---");

        // Prompt the user for the genre
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        // Validate the genre input
        if (!BookInputValidator.isValidGenre(genre)) {
            System.out.println("Invalid genre.");
            return;
        }

        try {
            // Fetch books belonging to the specified genre from the service layer
            List<Book> bookList = bookService.getBooksByGenre(genre);

            if (!bookList.isEmpty()) {
                // Display the list of books in the specified genre
                System.out.println("Books in the '" + genre + "' genre:");
                bookList.forEach(System.out::println);
                System.out.println();
            } else {
                // Inform the user if no books were found for the given genre
                System.out.println("There are no books in this genre.\n");
            }
        }  catch (IllegalArgumentException illegalArgumentException) {
            // Handle any exception from invalid genre or internal validation
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays the details of a book based on its title.
     * <p>
     * This method prompts the user to enter a book title, validates the input,
     * and retrieves the book from the system using {@code BookServiceImpl}.
     * If a book with the specified title exists, it is displayed; otherwise,
     * the user is informed that no such book was found.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code IllegalArgumentException} – if input is invalid or violates business rules</li>
     * </ul>
     */
    public void viewBookByTitle() {
        System.out.println("\n--- View Book by Title ---");

        // Prompt the user for the book title
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();

        // Validate the title input
        if (!BookInputValidator.isValidTitle(title)) {
            System.out.println("Invalid title.");
            return;
        }

        try {
            // Retrieve the book with the specified title
            Book book = bookService.getBookByTitle(title);

            if (book != null) {
                // Display the book details if found
                System.out.println(book + "\n");
            } else {
                // Inform user that no book with the given title exists
                System.out.println("Book with title " + title + " does not exist.\n");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle invalid input or other service-level errors
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Displays all books written by a specified author.
     * <p>
     * This method prompts the user to enter an author's name, validates the input,
     * and retrieves the list of books written by that author using {@code BookServiceImpl}.
     * If books are found, they are displayed sorted by title. Otherwise, it notifies the user
     * that no books were found for the given author.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code IllegalArgumentException} – if the author name is invalid or if business rules are violated</li>
     * </ul>
     */
    public void viewBooksByAuthor() {
        System.out.println("\n--- View Books by Author ---");

        // Prompt the user for the author's name
        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();

        // Validate the author's name
        if (!BookInputValidator.isValidAuthor(author)) {
            System.out.println("Invalid author.");
            return;
        }

        try {
            // Fetch all books written by the specified author
            List<Book> bookList = bookService.getBooksByAuthor(author);

            if (!bookList.isEmpty()) {
                // Sort books by title for consistent display
                bookList.sort(Comparator.comparing(Book::getTitle));

                // Display books by the author
                System.out.println("Books by '" + author + "':");
                bookList.forEach(System.out::println);
                System.out.println();
            } else {
                // Inform the user if no books are found for the given author
                System.out.println("No books exist for the specified author.\n");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle any input validation or service-level exception
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Searches and displays books that match a given keyword.
     * <p>
     * This method prompts the user to enter a search keyword, then uses the
     * {@code BookServiceImpl} to retrieve a list of books where the keyword
     * matches the title, author, genre, or ISBN. If matches are found,
     * they are displayed; otherwise, the user is informed that no results were found.
     * </p>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code IllegalArgumentException} – if the keyword is null, empty, or violates validation rules</li>
     * </ul>
     */
    public void searchBooks() {
        System.out.println("\n--- Search Books by Keyword ---");

        // Prompt the user for the search keyword
        System.out.print("Enter keyword to search by: ");
        String keyword = scanner.nextLine().trim();

        try {
            // Use the service to search books by keyword
            List<Book> bookList = bookService.searchBooks(keyword);

            if (!bookList.isEmpty()) {
                // Display matched books
                System.out.println("Matched Books: ");
                bookList.forEach(System.out::println);
                System.out.println(); // For spacing
            } else {
                // Inform the user if no matches were found
                System.out.println("No matching books found.\n");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            // Handle invalid keyword or other service-related exceptions
            System.out.println(illegalArgumentException.getMessage() + "\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a specific field of an existing book in the library.
     * <p>
     * This method prompts the user to enter a book ID, choose a field to update
     * (e.g., title, author, genre, ISBN, or number of total copies), and input the new value.
     * It validates inputs and ensures business rules (such as available copies not exceeding total copies)
     * are enforced. If valid, the specified field is updated using {@code BookServiceImpl}.
     * </p>
     *
     * <p><strong>Special Logic:</strong></p>
     * <ul>
     *   <li>When updating the {@code number_of_total_copies}, borrowed copies must not exceed the new total.</li>
     *   <li>If new total copies is less than current available copies, the available copies are adjusted accordingly.</li>
     * </ul>
     *
     * <p><strong>Exceptions:</strong></p>
     * <ul>
     *   <li>{@code IllegalArgumentException} – for invalid inputs or rule violations</li>
     * </ul>
     */
    public void updateBookField() {
        System.out.println("\n--- Update Book Field ---");

        // Prompt for and validate book ID
        System.out.print("Enter book ID: ");
        String bookIdStr = scanner.nextLine();
        if (!BookInputValidator.isValidBookId(bookIdStr)) {
            System.out.println("Invalid book ID.");
            return;
        }

        int bookId = Integer.parseInt(bookIdStr);

        try {
            // Check if the book exists
            if (!bookService.bookExists(bookId)) {
                System.out.println("Book not found.\n");
                return;
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println(illegalArgumentException.getMessage() + "\n");
        }

        // Prompt the user to choose which field to update
        String field = getField();

        // If the input is invalid or user chooses to cancel, exit
        if (field.equalsIgnoreCase("invalid") || field.equalsIgnoreCase("return")) {
            return;
        }

        // Get new value for the selected field
        Object newValue = getNewValue(field);

        // If input was invalid or cancelled, exit
        if (newValue == null) {
            return;
        }

        // Special handling if the user is updating number_of_total_copies
        if (field.equalsIgnoreCase("number_of_total_copies")) {

            try {
                int totalCopies = Integer.parseInt(newValue.toString());

                // Calculate how many copies are currently borrowed
                int borrowedCopies = bookService.getTotalCopies(bookId)
                        - bookService.getAvailableCopies(bookId);

                // Ensure total copies is not less than borrowed copies
                if (totalCopies < borrowedCopies) {
                    throw new IllegalArgumentException("Total copies cannot be less than borrowed copies.");
                }
                // If new total is less than current available, update available count too
                else if (totalCopies < bookService.getBookById(bookId).getNumberOfAvailableCopies()) {
                    bookService.updateNumberOfAvailableCopies(bookId, totalCopies);
                }

                // Attempt to update the field
                boolean fieldUpdated = bookService.updateBookField(bookId, field, newValue);

                if (fieldUpdated) {
                    System.out.println("Book field successfully updated.\n");
                } else {
                    System.out.println("Book field could not be updated.\n");
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("Error: " + illegalArgumentException.getMessage());
            }

        } else {
            // For all other fields
            try {

                boolean fieldUpdated = bookService.updateBookField(bookId, field, newValue);

                if (fieldUpdated) {
                    System.out.println("Book field successfully updated.\n");
                } else {
                    System.out.println("Book field could not be updated.\n");
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println("Error: " + illegalArgumentException.getMessage());
            }
        }

    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the user to select a book field to update and returns the corresponding field key.
     * <p>
     * This helper method presents a numbered list of editable book fields (e.g., Title, Author, Genre),
     * as well as options to return to the main admin menu or exit the program. It reads the user's input,
     * validates the selection, and returns a formatted field name (e.g., "title", "number_of_total_copies").
     * </p>
     *
     * <p><strong>Return Values:</strong></p>
     * <ul>
     *   <li>Formatted field name (e.g., {@code "isbn"}, {@code "genre"})</li>
     *   <li>{@code "return"} – if the user chooses to return to the main menu</li>
     *   <li>{@code "invalid"} – if input is invalid</li>
     * </ul>
     *
     * <p><strong>Note:</strong> If the user selects the "Exit" option, the program will terminate.</p>
     *
     * @return the selected field name in a format suitable for database/service updates
     */
    private String getField() {
        // List of editable fields
        String[] fields = {"Title", "Author", "Genre", "ISBN", "Number of Total Copies"};

        // Display menu options to the user
        System.out.println("\nSelect the field to update: ");
        for (int i = 0; i < fields.length; i++) {
            System.out.println((i + 1) + ". " + fields[i]);
        }
        System.out.println((fields.length + 1) + ". Return to the main admin menu");
        System.out.println((fields.length + 2) + ". Exit the program");

        try {
            // Prompt for user input
            System.out.print("Enter your choice: ");
            String userChoiceStr = scanner.nextLine();
            int userChoice = Integer.parseInt(userChoiceStr);

            // Validate menu option range
            if (userChoice < 1 || userChoice > fields.length + 2) {
                System.out.println("Invalid input, try again.\n");
                return "invalid";
            }

            // Option to return to the main menu
            if (userChoice == fields.length + 1) {
                return "return";
            }

            // Option to exit the program
            if (userChoice == fields.length + 2) {
                exitProgram(); // Terminates the program
            }

            // Return the selected field in a normalized format (e.g., "number_of_total_copies")
            return fields[userChoice - 1].toLowerCase().replaceAll("\\s+", "_");

        } catch (NumberFormatException e) {
            // Handle non-numeric input
            System.out.println("Invalid input, please enter a valid number.\n");
        }

        return "invalid"; // Return invalid if any error occurs
    }

    //-----------------------------------------------------------------------
    /**
     * Prompts the user to enter a new value for the specified book field and validates it.
     * <p>
     * This helper method is invoked during field-level book updates. Based on the field provided,
     * it requests the corresponding new value from the user, validates it using
     * {@code BookInputValidator}, and returns the validated value.
     * If the input is invalid, it prints an error message and returns {@code null}.
     * </p>
     *
     * @param field the name of the book field to be updated (e.g., "title", "isbn")
     * @return the validated new value as an {@code Object}, or {@code null} if validation fails
     */
    private Object getNewValue(String field) {
        switch (field) {
            case "title" -> {
                // Prompt and validate new book title
                System.out.print("\nEnter the new title: ");
                String title = scanner.nextLine();
                if (!BookInputValidator.isValidTitle(title)) {
                    System.out.println("Invalid title.");
                    return null;
                }

                return title;
            }
            case "author" -> {
                // Prompt and validate new author name
                System.out.print("\nEnter the new author name: ");
                String author = scanner.nextLine();
                if (!BookInputValidator.isValidAuthor(author)) {
                    System.out.println("Invalid author.");
                    return null;
                }

                return author;
            }
            case "genre" -> {
                // Prompt and validate new genre
                System.out.print("\nEnter new genre: ");
                String genre = scanner.nextLine();
                if (!BookInputValidator.isValidGenre(genre)) {
                    System.out.println("Invalid genre.");
                    return null;
                }

                return genre;
            }
            case "isbn" -> {
                // Prompt and validate new ISBN
                System.out.print("\nEnter new ISBN: ");
                String isbn = scanner.nextLine();
                if (!BookInputValidator.isValidIsbn(isbn)) {
                    System.out.println("Invalid ISBN, must be 9 digits with the 10th index either a digit or a character," +
                            "or the ISBN must be exactly 13 digits.");
                    return null;
                }

                return isbn;
            }
            case "number_of_total_copies" -> {
                // Prompt and validate new total copies count
                System.out.print("\nEnter number of total copies: ");
                String totalCopiesStr = scanner.nextLine();
                if (!BookInputValidator.isValidNumberOfTotalCopies(totalCopiesStr)) {
                    System.out.println("Invalid input, enter a valid number greater than 0.");
                    return null;
                }

                return totalCopiesStr;
            }
        }

        // Fallback if the field is not recognized
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Allows a member to borrow a book from the library.
     * <p>
     * This method checks if the user has already reached their borrowing limit (5 books).
     * If not, it allows the user to search for a book by title, choose one from the results,
     * and borrow it if copies are available and the user hasn't already borrowed the same book.
     * The borrowing is recorded via {@code TransactionServiceImpl}.
     * </p>
     *
     * <p><strong>Constraints:</strong></p>
     * <ul>
     *   <li>Users cannot borrow more than 5 books at a time.</li>
     *   <li>Users cannot borrow multiple copies of the same book.</li>
     *   <li>Only available books can be borrowed.</li>
     * </ul>
     *
     * <p><strong>Exceptions Handled:</strong></p>
     * <ul>
     *   <li>{@code UserNotFoundException} – if the member ID is invalid or not found</li>
     *   <li>{@code NoAvailableCopiesException} – if the selected book has no copies left</li>
     *   <li>{@code IndexOutOfBoundsException} – if user selects an invalid book number</li>
     * </ul>
     *
     * @param memberId the ID of the member attempting to borrow a book
     */
    public void borrowBook(int memberId) {
        // Retrieve books currently borrowed by the member
        List<Book> borrowedBooks = bookService.getBorrowedBooksByUser(memberId);

        // Check borrowing limit
        if (borrowedBooks.size() >= 5) {
            System.out.println("You have reached your borrowing limit (5 books). Please return a book before borrowing another.\n");
            return;
        }

        System.out.println("\n--- Borrow a Book ---");

        // Prompt user for book title
        System.out.print("Enter book title: ");
        String searchTitle = scanner.nextLine().trim();

        try {
            // Search for books matching the entered title
            List<Book> books = bookService.searchBooks(searchTitle);

            if (!books.isEmpty()) {
                // Display matched books with indices
                System.out.println("Books under the title '" + searchTitle + "':");
                for (int i = 0; i < books.size(); i++) {
                    System.out.println((i + 1) + ". " + books.get(i));
                }

                // Prompt user to select a book by index
                System.out.print("\nEnter book number to borrow: ");
                String userChoiceStr = scanner.nextLine();
                int selectedBook = Integer.parseInt(userChoiceStr);
                Book book = books.get(selectedBook - 1);

                // Prevent user from borrowing multiple copies of the same book
                if (transactionService.isBookBorrowedByUser(memberId, book.getBookId())) {
                    System.out.println("Cannot borrow more than one copy at a time, you have already borrowed a copy of this book.\n");
                    return;
                }

                // Attempt to borrow the book
                boolean bookBorrowed = transactionService.borrowBook(
                        new Transaction(
                                memberId,
                                book.getBookId(),
                                LocalDate.now(),    // Borrow date
                                null,               // Return date (null initially)
                                Status.BORROWED     // Transaction status
                        )
                );

                // Output result of borrowing attempt
                if (bookBorrowed) {
                    System.out.println("Book successfully borrowed.\n ");
                } else {
                    System.out.println("Book could not be borrowed. No available copies in the library at the moment.\n");
                }

            } else {
                // No books matched the entered title
                System.out.println("No books with title '" + searchTitle + "' exists.\n");
            }

        } catch (UserNotFoundException | NoAvailableCopiesException userNotFoundException) {
            // Handle specific business exceptions
            System.out.println(userNotFoundException.getMessage());

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // Handle invalid selection index
            System.out.println("Invalid input, please choose from the numbers provided.\n");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Allows a member to return a book they have previously borrowed.
     * <p>
     * This method first checks if the member has any currently borrowed books.
     * If they do, it prompts the user to select one from the list and attempts to
     * return it by updating the corresponding transaction via {@code TransactionServiceImpl}.
     * </p>
     *
     * <p><strong>Flow:</strong></p>
     * <ul>
     *   <li>Display borrowed books</li>
     *   <li>Let the user select which book to return</li>
     *   <li>Retrieve transaction ID</li>
     *   <li>Mark the transaction as returned</li>
     * </ul>
     *
     * <p><strong>Exceptions Handled:</strong></p>
     * <ul>
     *   <li>{@code IndexOutOfBoundsException} – if selected index is invalid</li>
     *   <li>{@code NumberFormatException} – if input is not a valid number</li>
     *   <li>{@code IllegalArgumentException} – for invalid transaction states or data errors</li>
     * </ul>
     *
     * @param memberId the ID of the member returning a book
     */
    public void returnBook(int memberId) {
        // Fetch list of books currently borrowed by the user
        List<Book> borrowedBooks =  bookService.getBorrowedBooksByUser(memberId);

        // If no borrowed books, notify and return
        if (borrowedBooks.isEmpty()) {
            System.out.println("You have not borrowed any books.\n");
            return;
        }

        try {
            System.out.println("\n--- Return Book ---");

            // Display list of borrowed books
            System.out.println("\nBorrowed Books:");
            for (int i = 0; i < borrowedBooks.size(); i++) {
                System.out.println((i + 1) + ". " + borrowedBooks.get(i));
            }

            // Prompt user to select which book to return
            System.out.print("Enter book number to return: ");
            String selectBookStr = scanner.nextLine();
            int selectedBook = Integer.parseInt(selectBookStr); // Convert input to integer index

            // Retrieve the transaction ID for the selected book and user.
            // This fetches the first transaction (if any) where:
            // - the book is currently marked as BORROWED (i.e., not yet returned),
            // - the transaction belongs to the specified member,
            // - and it matches the selected book ID.
            // This ensures that if the user has borrowed and returned this book multiple times,
            // only the active borrowing transaction is considered.
            int transactionId = transactionService.getTransactionIdForReturnBook(
                    memberId,
                    borrowedBooks.get(selectedBook - 1).getBookId()
            );

            // If no valid transaction is found
            if (transactionId == -1) {
                System.out.println("No transactions exist.");
                return;
            }

            // Attempt to return the book
            boolean bookReturned = transactionService.returnBook(transactionId, LocalDate.now());

            // Inform the user of the result
            if (bookReturned) {
                System.out.println("Book successfully returned.\n");
            } else {
                System.out.println("Book could not be returned.\n");
            }

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // Handle invalid selection index
            System.out.println("Invalid option selected. Please select from the numbers provided.\n");

        } catch (NumberFormatException numberFormatException) {
            // Handle non-numeric input
            System.out.println("Invalid input, enter a valid number.\n");

        }
        catch (IllegalArgumentException illegalArgumentException) {
            // Handle unexpected business rule violations
            System.out.println("Error: " + illegalArgumentException.getMessage());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gracefully terminates the application.
     * <p>
     * This utility method displays a goodbye message to the user and then terminates the program
     * using {@code System.exit(0)}. It is typically invoked when a user explicitly chooses
     * to exit from the application menu.
     * </p>
     *
     * <p><strong>Note:</strong> The exit status {@code 0} indicates a normal termination.</p>
     */
    private void exitProgram() {
        // Notify the user that the program is ending
        System.out.println("Exiting the program. Goodbye!");

        // Exit the application with status code 0 (successful termination)
        exit(0);
    }

}
