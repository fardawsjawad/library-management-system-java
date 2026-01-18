package com.library.management.service;



import com.library.management.dao.BookDAO;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.model.Book;
import com.library.management.model.User;
import com.library.management.model.User_Type;

import java.util.List;

/**
 * Class: BookServiceImpl
 *
 * Description:
 * - Implements the BookService interface to manage book-related business logic in the Library Management System.
 * - Handles validation, exception handling, and interaction with the BookDAO for persistence.
 * - Also interacts with UserServiceImpl to ensure user-related rules during book borrowing operations.
 *
 * Dependencies:
 * - BookDAO: Performs database operations related to books.
 * - UserServiceImpl: Used to retrieve user data and validate borrowing permissions.
 *
 * Key Responsibilities:
 * - Add, update, delete, and retrieve book data
 * - Manage book availability and track borrowed copies
 * - Perform search/filter operations on books
 * - Enforce business rules related to user-book interactions
 *
 * @author Fardaws Jawad
 */
public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;
    private final UserServiceImpl userService;

    //-----------------------------------------------------------------------
    /**
     * Constructor initializes DAO and dependent services.
     */
    public BookServiceImpl() {
        this.bookDAO = new BookDAO();
        this.userService = new UserServiceImpl();
    }


    //-----------------------------------------------------------------------
    /**
     * Adds a new book to the system.
     *
     * @param book Book - The book to be added
     * @return boolean - true if successfully added, false otherwise
     * @throws IllegalArgumentException - if the book, title, or author is null
     */
    @Override
    public boolean addBook(Book book) {
        if(book == null || book.getTitle() == null || book.getAuthor() == null) {
            throw new IllegalArgumentException("Book, title, and author must not be null.");
        }

        return bookDAO.addBook(book);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book by its ID.
     *
     * @param bookId int - ID of the book
     * @return Book - The book if found
     * @throws IllegalArgumentException - if bookId is invalid
     * @throws BookNotFoundException - if no book is found with the given ID
     */
    @Override
    public Book getBookById(int bookId) {
        if (bookId <= 0) throw new IllegalArgumentException("Invalid book ID");

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        return bookDAO.getBookById(bookId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all books in the system.
     *
     * @return List<Book> - List of all books
     */
    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the details of a book.
     *
     * @param book Book - Updated book object
     * @return boolean - true if updated successfully, false otherwise
     * @throws IllegalArgumentException - if book or bookId is invalid
     * @throws BookNotFoundException - if book does not exist
     */
    @Override
    public boolean updateBook(Book book) {
        if(book == null || book.getBookId() <= 0) {
            throw new IllegalArgumentException("Book and valid ID required for update.");
        }

        Book book1 = bookDAO.getBookById(book.getBookId());
        if (book1 == null) {
            throw new BookNotFoundException("Book with ID " + book.getBookId() + " does not exist.");
        }

        return bookDAO.updateBook(book);
    }

    //-----------------------------------------------------------------------
    /**
     * Deletes a book by its ID.
     *
     * @param bookId int - ID of the book
     * @return boolean - true if deleted successfully
     * @throws BookNotFoundException - if book is not found
     * @throws IllegalArgumentException - if copies are still issued
     */
    @Override
    public boolean deleteBook(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if(book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        if(book.getNumberOfAvailableCopies() != book.getNumberOfTotalCopies()) {
            throw new IllegalArgumentException("Cannot delete book: Some copies are currently issued.");
        }

        return bookDAO.deleteBook(bookId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all currently available books.
     *
     * @return List<Book> - List of books available for borrowing
     */
    @Override
    public List<Book> getAvailableBooks() {
        return bookDAO.getAvailableBooks();
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves books by genre.
     *
     * @param genre String - Genre name
     * @return List<Book> - List of books in the given genre
     * @throws IllegalArgumentException - if genre is null or blank
     */
    @Override
    public List<Book> getBooksByGenre(String genre) {
        if(genre == null || genre.isBlank()) {
            throw new IllegalArgumentException("Genre cannot be null or blank.");
        }

        return bookDAO.getBooksByGenre(genre);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book by its title.
     *
     * @param title String - Book title
     * @return Book - The matching book
     * @throws IllegalArgumentException - if title is null or blank
     */
    @Override
    public Book getBookByTitle(String title) {
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        return bookDAO.getBookByTitle(title);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves books by author.
     *
     * @param author String - Author's name
     * @return List<Book> - List of books by the author
     * @throws IllegalArgumentException - if author is null or blank
     */
    @Override
    public List<Book> getBooksByAuthor(String author) {
        if(author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank.");
        }

        return bookDAO.getBooksByAuthor(author);
    }

    //-----------------------------------------------------------------------
    /**
     * Searches books using a keyword that may match title, author, or genre.
     *
     * @param keyword String - Search keyword
     * @return List<Book> - List of matched books
     * @throws IllegalArgumentException - if keyword is null or blank
     */
    @Override
    public List<Book> searchBooks(String keyword) {
        if(keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword must not be null or blank.");
        }
        return bookDAO.searchBooks(keyword);
    }

    //-----------------------------------------------------------------------
    /**
     * Updates a specific field of a book dynamically.
     *
     * @param bookId int - ID of the book
     * @param field String - Field name to be updated
     * @param newValue Object - New value to set
     * @return boolean - true if update is successful
     * @throws IllegalArgumentException - if parameters are invalid
     */
    @Override
    public boolean updateBookField(int bookId, String field, Object newValue) {
        if(bookId <= 0 || field == null || field.isBlank() || newValue == null) {
            throw new IllegalArgumentException("Invalid book ID, field, or value.");
        }

        return bookDAO.updateBookField(bookId, field, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves books currently borrowed by a specific user.
     *
     * @param userId int - User ID
     * @return List<Book> - List of borrowed books
     * @throws IllegalArgumentException - if user ID is invalid or user is ADMIN
     * @throws UserNotFoundException - if user does not exist
     */
    @Override
    public List<Book> getBorrowedBooksByUser(int userId) {
        if (userId < 0) throw new IllegalArgumentException("Invalid user ID.");

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        if (user.getUserType().equals(User_Type.ADMIN)) {
            throw new IllegalArgumentException("Book borrowing is restricted to members only. Administrators cannot borrow books.");
        }

        return bookDAO.getBorrowedBooksByUser(userId);
    }

    //-----------------------------------------------------------------------
    /**
     * Retrieves all books currently borrowed in the system.
     *
     * @return List<Book> - List of all borrowed books
     */
    @Override
    public List<Book> getAllBorrowedBooks() {
        return bookDAO.getAllBorrowedBooks();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if a book exists by its ID.
     *
     * @param bookId int - Book ID
     * @return boolean - true if the book exists, false otherwise
     * @throws IllegalArgumentException - if bookId is invalid
     */
    public boolean bookExists(int bookId) {
        if (bookId <= 0) throw new IllegalArgumentException("Invalid user ID.");
        return bookDAO.getBookById(bookId) != null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the total number of copies for a specific book.
     *
     * @param bookId int - Book ID
     * @return int - Total number of copies
     */
    public int getTotalCopies(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        return book.getNumberOfTotalCopies();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of currently available copies for a book.
     *
     * @param bookId int - Book ID
     * @return int - Available copies
     */
    public int getAvailableCopies(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        return book.getNumberOfAvailableCopies();
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the number of available copies for a book.
     *
     * @param bookId int - Book ID
     * @param numberOfAvailableCopies int - New number of available copies
     * @return boolean - true if update is successful
     */
    @Override
    public boolean updateNumberOfAvailableCopies(int bookId, int numberOfAvailableCopies) {
        return bookDAO.updateNumberOfAvailableCopies(bookId, numberOfAvailableCopies);
    }

}
