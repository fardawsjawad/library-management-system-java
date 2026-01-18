package com.library.management.exception;

/**
 * Exception thrown when a requested book is not found in the system.
 * <p>
 * This custom exception is typically used in service or DAO layers
 * to indicate that the specified book ID or title does not exist
 * in the database.
 *
 * @see com.library.management.dao.BookDAO
 * @see com.library.management.service.BookService
 */
public class BookNotFoundException extends RuntimeException{
    /**
     * Constructs a new BookNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BookNotFoundException(String message) {
        super (message);
    }
}
