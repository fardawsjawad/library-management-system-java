package com.library.management.service;

import com.library.management.model.Book;

import java.util.List;

/**
 * Interface: BookService
 *
 * Description:
 * - Defines the contract for book-related operations in the Library Management System.
 * - Supports CRUD operations, advanced search, availability tracking, and borrowing status.
 * - Provides flexibility to retrieve and update book data based on various criteria like genre, title, author, etc.
 *
 * Key Responsibilities:
 * - Add, retrieve, update, and delete book records
 * - Search and filter books by title, author, genre, and keywords
 * - Track total and available copies of books
 * - Manage borrowed books per user and system-wide
 *
 * @author Fardaws Jawad
 */
public interface BookService {

    //-----------------------------------------------------------------------
    /**
     * Adds a new book to the library.
     *
     * @param book Book - The book object to be added
     * @return boolean - true if added successfully, false otherwise
     */
    boolean addBook(Book book);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book by its ID.
     *
     * @param bookId int - The unique ID of the book
     * @return Book - The corresponding book object if found, otherwise null
     */
    Book getBookById(int bookId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves all books from the system.
     *
     * @return List<Book> - A list of all book records
     */
    List<Book> getAllBooks();

    //-----------------------------------------------------------------------
    /**
     * Updates an existing book's details.
     *
     * @param book Book - The updated book object
     * @return boolean - true if update is successful, false otherwise
     */
    boolean updateBook(Book book);

    //-----------------------------------------------------------------------
    /**
     * Deletes a book by its ID.
     *
     * @param bookId int - The ID of the book to delete
     * @return boolean - true if deletion is successful, false otherwise
     */
    boolean deleteBook(int bookId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves all books that are currently available for borrowing.
     *
     * @return List<Book> - A list of available books
     */
    List<Book> getAvailableBooks();

    //-----------------------------------------------------------------------
    /**
     * Retrieves books by their genre.
     *
     * @param genre String - The genre to filter books by
     * @return List<Book> - A list of books in the given genre
     */
    List<Book> getBooksByGenre(String genre);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a book by its title.
     *
     * @param title String - The title of the book
     * @return Book - The book object if found, otherwise null
     */
    Book getBookByTitle(String title);

    //-----------------------------------------------------------------------
    /**
     * Retrieves books written by a specific author.
     *
     * @param author String - The author's name
     * @return List<Book> - A list of books by the given author
     */
    List<Book> getBooksByAuthor(String author);

    //-----------------------------------------------------------------------
    /**
     * Searches books using a keyword that may match title, author, or genre.
     *
     * @param keyword String - The keyword for search
     * @return List<Book> - A list of matching books
     */
    List<Book> searchBooks(String keyword);

    //-----------------------------------------------------------------------
    /**
     * Dynamically updates a specific field of a book by its ID.
     *
     * @param bookId int - The ID of the book to update
     * @param field String - The name of the field to update
     * @param newValue Object - The new value for the field
     * @return boolean - true if update is successful, false otherwise
     */
    boolean updateBookField(int bookId, String field, Object newValue);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of books borrowed by a specific user.
     *
     * @param userId int - The ID of the user
     * @return List<Book> - A list of books currently borrowed by the user
     */
    List<Book> getBorrowedBooksByUser(int userId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves a list of all currently borrowed books in the system.
     *
     * @return List<Book> - A list of all borrowed books
     */
    List<Book> getAllBorrowedBooks();

    //-----------------------------------------------------------------------
    /**
     * Checks whether a book exists in the system based on its ID.
     *
     * @param bookId int - The book ID to check
     * @return boolean - true if the book exists, false otherwise
     */
    boolean bookExists(int bookId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the total number of copies of a specific book.
     *
     * @param bookId int - The ID of the book
     * @return int - The total number of copies
     */
    int getTotalCopies(int bookId);

    //-----------------------------------------------------------------------
    /**
     * Retrieves the number of available copies of a specific book.
     *
     * @param bookId int - The ID of the book
     * @return int - The number of copies available
     */
    int getAvailableCopies(int bookId);

    //-----------------------------------------------------------------------
    /**
     * Updates the number of available copies for a book.
     *
     * @param bookId int - The ID of the book
     * @param numberOfAvailableCopies int - The new number of available copies
     * @return boolean - true if update is successful, false otherwise
     */
    boolean updateNumberOfAvailableCopies(int bookId, int numberOfAvailableCopies);

}
